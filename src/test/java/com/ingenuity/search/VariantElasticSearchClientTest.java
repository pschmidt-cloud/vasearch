package com.ingenuity.search;

import com.ingenuity.search.JdbcVariantAnalysisDao;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.datehistogram.DateHistogramFacet;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.StopWatch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: pschmidt
 */
public class VariantElasticSearchClientTest {
    private static JdbcVariantAnalysisDao dao = new JdbcVariantAnalysisDao();
    private static TransportClient client = null;
    private static final String index = "vaindex";
    private static final String sample_type = "sample";
    private static final String analysis_type = "analysis";
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private String json = "{" +
        "\"timeCreated\":\"2013-01-31\"," +
        "\"name\":\"Sample from patient A \"" +
        "}";

    private String jsonSearchQuery =
        "{" + "\"term\" : {\"genome\" : \"hg18\" }}";

    public static DriverManagerDataSource getDataSource() throws Exception {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=scan-eng)(PORT=1537))(CONNECT_DATA=(SERVICE_NAME=TIVA)))");
        ds.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        ds.setUsername("ivauser");
        ds.setPassword("tiva_ivauser");

        return ds;
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        dao.setDataSource(getDataSource());
        /*
        #elastic_server_url = http://uieseapp1.ingenuity.com:8168
        elastic_server_url = http://localhost:8168
        elastic_server_index = emdindex1
        elastic_folder_type = folder
        elastic_observation_type = observation
       */
       // Node node = NodeBuilder.nodeBuilder().node();
        //client = node.client();

        Settings settings = ImmutableSettings.settingsBuilder()
            .put("cluster.name", "elasticsearch-lumacpschmidt").build();
            //.put("cluster.name", "elasticsearch-uedev5-1").build();

        client = new TransportClient(settings);
        client.addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
        //client.addTransportAddress(new InetSocketTransportAddress("uedev5.ingenuity.com", 8999));
    }

    @Test
    public void insertSample() throws Exception {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("age", "25");
        json.put("gender", "male");
        json.put("phenotype", "cancer");

        IndexResponse response = client.prepareIndex(index, sample_type, "10")
            .setSource(jsonBuilder()
                    .startObject()
                    .field("timeCreated", dateFormat.format(new Date()))
                    .field("name", "BRCA test")
                    .field("genome", "HG18")
                    .field("variants", "55")
                    .field("annotations", json)
                    .endObject()
            )
            .execute()
            .actionGet();
    }

    private String getGenome(int genome) {
         switch (genome) {
             case 0 : return "unknown";
             case 1 : return "hg18";
             case 2 : return "hg19";
             case 3 : return "hg20";
         }

        return "unknown";
    }

    private String getGender(int gender) {
        switch(gender) {
            case 0 : return "unknown";
            case 1 : return "male";
            case 2 : return "female";
            default : return "unknown";
        }
    }

    @Test
    public void insertAllSamplesViaDatabase() throws Exception {
        StopWatch sw = new StopWatch();
        sw.start("gettings samples from db");

        BulkRequestBuilder bulkRequest = client.prepareBulk();

        //List<JdbcVariantAnalysisDao.Sample> samples = dao.selectAllSamples(0, 450000);    //152455 results from *
        List<JdbcVariantAnalysisDao.Sample> samples = dao.selectAllSamples(358262, 559884);  // 158456 results from *

        // 358262, 558349

        for (JdbcVariantAnalysisDao.Sample sample : samples) {
            bulkRequest.add(client.prepareIndex(index, sample_type, String.valueOf(sample.getId()))
                .setSource(jsonBuilder()
                        .startObject()
                            //.field("timeCreated", new Date())
                        .field("name", sample.getName())
                        .field("variants", sample.getVariants())
                        .field("timeCreated", dateFormat.format(sample.getTimeCreated()))
                        .field("genome", getGenome(sample.getGenome()))
                        .field("gender", getGender(sample.getGender()))
                        .field("status", sample.getStatus())
                        .field("annotations", sample.getAnnotations())
                        .endObject()
                )
            );
        }

        BulkResponse bulkResponse = bulkRequest.execute().actionGet();

        sw.stop();
    }

    @Test
    public void insertSampleViaDatabase() throws Exception {
        JdbcVariantAnalysisDao.Sample sample = dao.selectSample(408061);

        IndexResponse response = client.prepareIndex(index, sample_type, String.valueOf(sample.getId()))
            .setSource(jsonBuilder()
                    .startObject()
                        //.field("timeCreated", new Date())
                    .field("name", sample.getName())
                    .field("variants", sample.getVariants())
                    .field("timeCreated", dateFormat.format(sample.getTimeCreated()))
                    .field("genome", getGenome(sample.getGenome()))
                    .field("annotations", sample.getAnnotations())
                    .endObject()
            )
            .execute()
            .actionGet();
    }

    @Test
    public void insertSampleViaJson() throws Exception {
        IndexResponse response = client.prepareIndex(index, sample_type, "11")
            .setSource(json)
            .execute()
            .actionGet();
    }

    @Test
    public void testGetSampleById() throws Exception {
        GetResponse response = client.prepareGet(index, sample_type, "10")
            .execute()
            .actionGet();

        System.out.println("response= " + response.getId());
    }

    @Test
    public void testDeleteSample() throws Exception {
        IndexResponse indexResponse = client.prepareIndex(index, sample_type, "44")
            .setSource(json)
            .execute()
            .actionGet();

        DeleteResponse response = client.prepareDelete(index, sample_type, "44")
            .execute()
            .actionGet();

        System.out.println("response= " + response.getId());
    }

    @Test
    public void testSearchSample() throws Exception {
        String query = "female OR cancer OR vcf ";
        String anyQuery = "{" + "\"query_string\" : {\"query\" : " + query + " }}";  // not working
        QueryBuilder qb = QueryBuilders.queryString(query);

        SearchResponse response = client.prepareSearch(index)
            .setTypes(sample_type)
            .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
            .setQuery(qb)             // Query
            .addHighlightedField("*")
            //.setPostFilter(FilterBuilders.rangeFilter("age").from(12).to(18))   // Filter
            .setFrom(0).setSize(60).setExplain(false)
            .execute()
            .actionGet();

        SearchHit[] results = response.getHits().getHits();
        for (SearchHit hit : results) {
            System.out.println("docId= " + hit.getId());    //prints out the id of the document
            Map<String, Object> result = hit.getSource();   //the retrieved document
            System.out.println("#highlighted= " + hit.getHighlightFields().size());
            System.out.println(result);
        }

        System.out.println("hits= " + response.getHits().getTotalHits());
        System.out.println("json=" + response.toString());
    }

    @Test
    public void testFacet() throws Exception {
        SearchResponse sr = client.prepareSearch(index)
            .setQuery(QueryBuilders.matchAllQuery())
            .addFacet(FacetBuilders.termsFacet("f1").field("annotations.Gender"))
            //.addFacet(FacetBuilders.dateHistogramFacet("f2").field("timeCreated").interval("year"))
            .execute().actionGet();

        // Get facet results
        TermsFacet f1 = (TermsFacet) sr.getFacets().facetsAsMap().get("f1");
        DateHistogramFacet f2 = (DateHistogramFacet) sr.getFacets().facetsAsMap().get("f2");

        System.out.println("f1 count=" + f1.getTotalCount());
        //System.out.println("f2=" + f2.getName());
    }

    @Test
    public void testFacetSearch() throws Exception {
        SearchResponse sr = client.prepareSearch(index)
            .setQuery(jsonSearchQuery)
            .addFacet(FacetBuilders.termsFacet("f1").field("annotations.owner"))
                //.addFacet(FacetBuilders.dateHistogramFacet("f2").field("timeCreated").interval("year"))
            .execute().actionGet();

        // Get facet results
        TermsFacet f1 = (TermsFacet) sr.getFacets().facetsAsMap().get("f1");
        DateHistogramFacet f2 = (DateHistogramFacet) sr.getFacets().facetsAsMap().get("f2");

        System.out.println("f1 count=" + f1.getTotalCount());
        System.out.println("json resonse string= " + sr.toString());
        //System.out.println("f2=" + f2.getName());
    }


}
