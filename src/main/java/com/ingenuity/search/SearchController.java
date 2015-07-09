package com.ingenuity.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingenuity.model.Comment;
import com.ingenuity.model.VariantAssessment;
import com.ingenuity.service.AssessmentService;
import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.facet.FacetBuilders;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SearchController implements InitializingBean {
    private Logger log = Logger.getLogger(SearchController.class);

    @Autowired
    AssessmentService assessmentService;

    private TransportClient client;

    @Override
    public void afterPropertiesSet() throws Exception {
        Settings settings = ImmutableSettings.settingsBuilder()
            .put("cluster.name", "elasticsearch-uedev5-1").build();

        client = new TransportClient(settings);
        client.addTransportAddress(new InetSocketTransportAddress("uedev5.ingenuity.com", 8999));
    }

    @RequestMapping(value = "/searchPost/{queryString}", method = RequestMethod.POST)
    @ResponseBody
    public HttpStatus
    searchPost(@PathVariable(value = "queryString") String queryString,
                         HttpServletResponse response) throws Exception {

        return HttpStatus.OK;
    }

    @RequestMapping(value = "/search/{queryString}", method = RequestMethod.GET)
    @ResponseBody
    public Map searchGet(@PathVariable(value = "queryString") String queryString,
                      HttpServletResponse response) throws Exception {
        QueryBuilder qb = QueryBuilders.queryString(queryString).defaultOperator(QueryStringQueryBuilder.Operator.AND);
        SearchResponse sr = null;
        Map<String, Object> returnMap = new HashMap<String, Object>();
        Map<String, Object> resultMap = new HashMap<String, Object>();

        try {
            sr = client.prepareSearch("vaindex")
                .setQuery(qb)
                    .addHighlightedField("*")
                    .setHighlighterPreTags("<mark>")
                    .setHighlighterPostTags("</mark>")
                    .addFacet(FacetBuilders.termsFacet("gender").field("gender"))
                    .addFacet(FacetBuilders.termsFacet("genome").field("genome"))
                    .addFacet(FacetBuilders.termsFacet("ethnicity").field("annotations.ethnicity"))
                .execute().actionGet();
        } catch (Exception e) {
            log.error(e);
            returnMap.put("results", -1);
            return returnMap;
        }

        ObjectMapper mapper = new ObjectMapper();
        Map resultsMap = mapper.readValue(sr.toString(), HashMap.class);

        returnMap.put("results", resultsMap);

        return returnMap;
    }

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    @ResponseBody
    public String json() {
        return "Hello, world of json!";
    }

    @RequestMapping("/comment")
    @ResponseBody
    public Map comment() {
        Comment comment1 = new Comment();
        comment1.setAuthor("pschmidt@ingenuity.com");

        Comment comment2 = new Comment();
        comment2.setAuthor("theone@ingenuity.com");

        List<Comment> comments = new ArrayList<Comment>();
        comments.add(comment1);
        comments.add(comment2);

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("results", comments);

        log.info("size= " + resultMap.size());

        return resultMap;
    }

    @RequestMapping("/findAssessment/{id}")
    @ResponseBody
    public VariantAssessment findAssessment(@PathVariable(value = "id") String id) {
        return assessmentService.findOne(id);
    }
}
