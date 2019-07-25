package com.atguigu.gmall.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SkuLsParams;
import com.atguigu.gmall.bean.SkuLsResult;
import com.atguigu.gmall.config.RedisUtil;
import com.atguigu.gmall.service.ListService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ListServiceImpl implements ListService{

     @Autowired
     JestClient jestClient;
     @Autowired
     RedisUtil redisUtil;

     public static final String ES_INDEX="gmall";

     public static final String ES_TYPE="SkuInfo";

    @Override
    public void saveSkuInfo(SkuLsInfo skuLsInfo) {
        //保存数据
        //PUT /gmall/SkuInfo/skuLsInfo.getId()
        Index index=new Index.Builder(skuLsInfo).index(ES_INDEX).type(ES_TYPE).id(skuLsInfo.getId()).build();
         try {
             //执行操作
             jestClient.execute(index);
         }catch (IOException e){
             e.printStackTrace();
         }
    }


    @Override
    public SkuLsResult search(SkuLsParams skuLsParams) {
        /*
        1.  定义一个dsl 语句
        2.  将dsl 语句放入查询器中
        3.  执行查询
        4.  将执行的结果集返回
         */
        String query = makeQueryStringForSearch(skuLsParams);

        Search search = new Search.Builder(query).addIndex(ES_INDEX).addType(ES_TYPE).build();
        SearchResult searchResult =null;
        try {
            searchResult = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 调用一个方法，将其searchResult 转换为我们需要的实体类对象
        SkuLsResult skuLsResult = makeResultForSearch(skuLsParams,searchResult);

        return skuLsResult;
    }

    @Override
    public void incrHotScore(String skuId) {
        Jedis jedis = redisUtil.getJedis();
        int timesToEs=10;
        Double hotScore = jedis.zincrby("hotScore", 1, "skuId:" + skuId);
        if(hotScore%timesToEs==0){
            updateHotScore(skuId,  Math.round(hotScore));
        }
    }

    private void updateHotScore(String skuId,Long hotScore){
        String updateJson="{\n" +
                "   \"doc\":{\n" +
                "     \"hotScore\":"+hotScore+"\n" +
                "   }\n" +
                "}";

        Update update = new Update.Builder(updateJson).index("gmall").type("SkuInfo").id(skuId).build();
        try {
            jestClient.execute(update);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 制作返回结果集
     * @param skuLsParams
     * @param searchResult
     * @return
     */
    private SkuLsResult makeResultForSearch(SkuLsParams skuLsParams, SearchResult searchResult) {
        // 返回的结果集skuLsResult
        SkuLsResult skuLsResult = new SkuLsResult();
        // 给其赋值
        //        private List<SkuLsInfo> skuLsInfoList;

        // 声明一个集合来存储skuLsInfo 对象
        ArrayList<SkuLsInfo> skuLsInfoList = new ArrayList<>();

        // 所有的商品信息
        List<SearchResult.Hit<SkuLsInfo, Void>> hits = searchResult.getHits(SkuLsInfo.class);
        if (hits!=null && hits.size()>0){
            // 循环遍历
            for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
                SkuLsInfo skuLsInfo = hit.source;

                if (hit.highlight!=null && hit.highlight.size()>0){
                    // 取出skuName 的高亮
                    Map<String, List<String>> map = hit.highlight;
                    // 根据skuName 取出带高亮显示的集合字符串
                    // <span style=color:red>小米</span>三<span style=color:red>代</span>
                    List<String> list = map.get("skuName");
                    String skuNameHi = list.get(0);
                    // 将其skuNameHi 放入skuLsInfo 中替换到skuName
                    skuLsInfo.setSkuName(skuNameHi);
                }


                // 将其添加到集合中
                skuLsInfoList.add(skuLsInfo);
            }
        }

        skuLsResult.setSkuLsInfoList(skuLsInfoList);

        //        private long total;
        skuLsResult.setTotal(searchResult.getTotal());

        //        private long totalPages;
        //  公式：如何计算总页数？ （总条数%每页显示的条数==0？总条数/每页显示的条数:总条数%每页显示的条数+1）
        //  int totalPage = (searchResult.getTotal()%skuLsParams.getPageSize()==0?searchResult.getTotal()/skuLsParams.getPageSize():searchResult.getTotal()/skuLsParams.getPageSize()+1)
        long totalPage = (searchResult.getTotal() + skuLsParams.getPageSize() -1)/skuLsParams.getPageSize();
        skuLsResult.setTotalPages(totalPage);
        //        private List<String> attrValueIdList;

        //  声明一个集合来存储平台属性值Id
        ArrayList<String> valueIdLIst = new ArrayList<>();
        // 获取属性值Id的集合 在聚合中获取平台属性值的Id
        MetricAggregation aggregations = searchResult.getAggregations();

        // term - Aggregation
        TermsAggregation groupby_attr = aggregations.getTermsAggregation("groupby_attr");
        List<TermsAggregation.Entry> buckets = groupby_attr.getBuckets();
        if (buckets!=null && buckets.size()>0){
            // 循环遍历
            for (TermsAggregation.Entry bucket : buckets) {
                String valueId = bucket.getKey();
                valueIdLIst.add(valueId);
            }
        }

        skuLsResult.setAttrValueIdList(valueIdLIst);
        return skuLsResult;
    }

    /**
     * 动态生成dsl 语句。
     * @param skuLsParams
     * @return
     */
    private String makeQueryStringForSearch(SkuLsParams skuLsParams) {

        // 创建一个查询器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // "bool"
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 判断输入的skuName === keyword 是否为空
        if (skuLsParams.getKeyword()!=null && skuLsParams.getKeyword().length()>0){
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName",skuLsParams.getKeyword());
            // bool -- must --- match
            boolQueryBuilder.must(matchQueryBuilder);
            // 设置高亮
            HighlightBuilder highlighter = searchSourceBuilder.highlighter();
            // 设置高亮字段,规则
            highlighter.field("skuName");
            highlighter.preTags("<span style=color:red>");
            highlighter.postTags("</span>");

            // 将设置之后的对象，放入查询器，使其生效
            searchSourceBuilder.highlight(highlighter);
        }

        // 操作三级分类Id
        if (skuLsParams.getCatalog3Id()!=null && skuLsParams.getCatalog3Id().length()>0){
            // 创建term 对象
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id",skuLsParams.getCatalog3Id());
            // bool -- filter --- term
            boolQueryBuilder.filter(termQueryBuilder);
        }

        // 设置平台属性值Id
        if (skuLsParams.getValueId()!=null && skuLsParams.getValueId().length>0){
            // 循环遍历
            for (String valueId : skuLsParams.getValueId() ) {
                // 创建term 对象
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId",valueId);
                // bool -- filter --- term
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
        // 分页 分页公式开始条数如何计算？
        // （pageNo - 1）* pageSize
        //  10 ，3 ， 1，2，3，4
        //  2：3
        //  3：6

        int from = (skuLsParams.getPageNo()-1)*skuLsParams.getPageSize();
        // 从第几条开始查询数据
        searchSourceBuilder.from(from);
        // 每页显示的大小
        searchSourceBuilder.size(skuLsParams.getPageSize());

        // 设置排序
        searchSourceBuilder.sort("hotScore", SortOrder.DESC);

        // 设置聚合aggs 规则
        TermsBuilder groupby_attr = AggregationBuilders.terms("groupby_attr").field("skuAttrValueList.valueId");

        // 将聚合对象放入查询器
        searchSourceBuilder.aggregation(groupby_attr);

        // "query"
        searchSourceBuilder.query(boolQueryBuilder);

        // 将其 searchSourceBuilder 变成字符串
        String query = searchSourceBuilder.toString();

        System.out.println("query:="+query);

        return query;
    }



}
