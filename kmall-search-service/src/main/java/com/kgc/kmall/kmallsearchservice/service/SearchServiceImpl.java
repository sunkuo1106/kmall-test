package com.kgc.kmall.kmallsearchservice.service;

import com.kgc.kmall.bean.PmsSearchSkuInfo;
import com.kgc.kmall.bean.PmsSearchSkuParam;
import com.kgc.kmall.bean.PmsSkuAttrValue;
import com.kgc.kmall.service.SearchService;
import org.apache.dubbo.config.annotation.Service;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Component
public class SearchServiceImpl implements SearchService {

    @Resource
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public List<PmsSearchSkuInfo> list(PmsSearchSkuParam pmsSearchSkuParam) {
        List<PmsSearchSkuInfo> pmsSearchSkuInfos=new ArrayList<>();
        //封装条件
        String catalog3Id = pmsSearchSkuParam.getCatalog3Id();
        String keyword = pmsSearchSkuParam.getKeyword();
        String[] valueId = pmsSearchSkuParam.getValueId();

        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();

        if (catalog3Id!=null){
            TermQueryBuilder termQueryBuilder=new TermQueryBuilder("catalog3Id",catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }
        if (valueId!=null){
            for (String vid : valueId) {
                TermQueryBuilder termQueryBuilder=new TermQueryBuilder("skuAttrValueList.valueId",vid);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
        if (keyword!=null&&keyword.isEmpty()==false){
            MatchQueryBuilder matchQueryBuilder=new MatchQueryBuilder("skuName",keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }

        //高亮
        HighlightBuilder highlightBuilder=new HighlightBuilder();
        highlightBuilder.field("skuName");
        highlightBuilder.preTags("<span style='color:red;'>");
        highlightBuilder.postTags("</span>");

        SearchQuery searchQuery=new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder).withHighlightBuilder(highlightBuilder)
                .build();

        pmsSearchSkuInfos = elasticsearchRestTemplate.queryForList(searchQuery, PmsSearchSkuInfo.class);

        //实现高亮处理
        AggregatedPage<PmsSearchSkuInfo> pmsSearchSkuInfos1 = elasticsearchRestTemplate.queryForPage(searchQuery, PmsSearchSkuInfo.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {

                ArrayList<PmsSearchSkuInfo> pmsSearchSkuInfos2 = new ArrayList<>();
                SearchHits hits = response.getHits();
                for (SearchHit searchHit : hits) {
                    if (hits.getHits().length <= 0) {
                        return null;
                    }

                    PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();

                    String highLightMessage = searchHit.getHighlightFields().get("skuName").fragments()[0].toString();
                    pmsSearchSkuInfo.setSkuName(highLightMessage);

                    pmsSearchSkuInfo.setId(Long.parseLong(searchHit.getId()));
                    pmsSearchSkuInfo.setProductId(Long.parseLong(searchHit.getSourceAsMap().get("productId").toString()));
                    pmsSearchSkuInfo.setCatalog3Id(Long.parseLong(searchHit.getSourceAsMap().get("catalog3Id").toString()));
                    pmsSearchSkuInfo.setPrice(Double.parseDouble(searchHit.getSourceAsMap().get("price").toString()));
                    pmsSearchSkuInfo.setSkuAttrValueList((List<PmsSkuAttrValue>) searchHit.getSourceAsMap().get("skuAttrValueList"));
                    pmsSearchSkuInfo.setSkuDefaultImg(searchHit.getSourceAsMap().get("skuDefaultImg").toString());
                    pmsSearchSkuInfo.setSkuDesc(searchHit.getSourceAsMap().get("skuDesc").toString());
                    pmsSearchSkuInfos2.add(pmsSearchSkuInfo);

                }
                if (pmsSearchSkuInfos2.size() > 0) {
                    return new AggregatedPageImpl<T>((List<T>) pmsSearchSkuInfos2);
                }
                return null;
            }
            @Override
            public <T> T mapSearchHit(SearchHit searchHit, Class<T> aClass) {
                return null;
            }
        });
        if (pmsSearchSkuInfos1!=null){
            return pmsSearchSkuInfos1.toList();
        }
        return pmsSearchSkuInfos;
    }
}
