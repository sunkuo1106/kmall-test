package com.kgc.kmall.kmallsearchservice;

import com.kgc.kmall.bean.PmsSearchSkuInfo;
import com.kgc.kmall.bean.PmsSkuInfo;
import com.kgc.kmall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import org.apache.dubbo.config.annotation.Reference;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class KmallSearchServiceApplicationTests {

    @Reference
    SkuService skuService;

    @Resource
    JestClient jestClient;

    @Resource
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    void contextLoads() {
        List<PmsSkuInfo> allSku = skuService.getAllSku();
        List<PmsSearchSkuInfo> pmsSearchSkuInfos=new ArrayList<>();
        for (PmsSkuInfo pmsSkuInfo : allSku) {
            PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
            BeanUtils.copyProperties(pmsSkuInfo,pmsSearchSkuInfo);
            pmsSearchSkuInfo.setProductId(pmsSkuInfo.getSpuId());
            pmsSearchSkuInfos.add(pmsSearchSkuInfo);
        }
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            Index index=new Index.Builder(pmsSearchSkuInfo).index("kmall").type("PmsSkuInfo").id(pmsSearchSkuInfo.getId()+"").build();
            try {
                jestClient.execute(index);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void test1(){
        List<PmsSkuInfo> allSku = skuService.getAllSku();
        System.out.println(allSku.size());
    }

    @Test
    void testSearch(){
        String json="\n" +
                "{\n" +
                "  \"query\": {\n" +
                "    \"bool\": {\n" +
                "      \"filter\": [\n" +
                "          {\"terms\":{\"skuAttrValueList.valueId\":[\"39\",\"40\",\"41\",\"42\"]}},\n" +
                "          {\"term\":{\"skuAttrValueList.valueId\":\"43\"}}\n" +
                "        ], \n" +
                "      \"must\": \n" +
                "        {\n" +
                "          \"match\": {\n" +
                "            \"skuName\": \"iphone\"\n" +
                "          }\n" +
                "        }\n" +
                "      \n" +
                "    }\n" +
                "  }\n" +
                "}";
        Search search=new Search.Builder(json).addIndex("kmall").addType("PmsSkuInfo").build();
        try {
            io.searchbox.core.SearchResult searchResult = jestClient.execute(search);
            List<io.searchbox.core.SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = searchResult.getHits(PmsSearchSkuInfo.class);
            for (io.searchbox.core.SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
                PmsSearchSkuInfo skuInfo = hit.source;
                System.out.println(skuInfo.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSearchBuilder(){
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();
        TermQueryBuilder termQueryBuilder=new TermQueryBuilder("skuAttrValueList.valueId","39");
        TermQueryBuilder termQueryBuilder2=new TermQueryBuilder("skuAttrValueList.valueId","43");

        boolQueryBuilder.filter(termQueryBuilder);
        boolQueryBuilder.filter(termQueryBuilder2);

        MatchQueryBuilder matchQueryBuilder=new MatchQueryBuilder("skuName","iphone");

        boolQueryBuilder.must(matchQueryBuilder);

        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);

        System.out.println(searchSourceBuilder.toString());
    }

    @Test
    public void testSearchBuilder2(){
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();
        boolQueryBuilder.must(new MatchQueryBuilder("skuName","iphone"));

        SearchQuery searchQuery=new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .build();

        List<PmsSearchSkuInfo> pmsSearchSkuInfos = elasticsearchRestTemplate.queryForList(searchQuery, PmsSearchSkuInfo.class);
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            System.out.println(pmsSearchSkuInfo.toString());
        }


    }

}
