package com.ingenuity.service;

import com.ingenuity.search.Application;
import com.ingenuity.model.Comment;
import com.ingenuity.model.VariantAssessment;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.action.search.SearchType.COUNT;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: pschmidt
 * Date: 7/2/15
 * Time: 10:07 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class AssessmentServiceTest {
    @Autowired
    AssessmentService assessmentService;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void testGetOne() {
        VariantAssessment assessment = assessmentService.findOne("3");

        assertTrue(assessment != null);
        assertEquals(3, assessment.getId());
    }

    @Test
    public void insertAssessment() {
        List<Comment> comments = new ArrayList<Comment>();

        Comment comment1 = new Comment();
        comment1.setAuthor("pschmidt@ingenuity.com");
        comment1.setCommentData("likely benign");
        comment1.setId(1);

        Comment comment2 = new Comment();
        comment2.setAuthor("pschmidt@ingenuity.com");
        comment2.setCommentData("benign");
        comment2.setId(2);
        comments.add(comment1);
        comments.add(comment2);

        VariantAssessment assessment = new VariantAssessment();
        assessment.setId(3);
        assessment.setAssessment("Benign");
        assessment.setGenotype("Het");
        assessment.setStatus("reportable");
        assessment.setPhenotype("Cancer");
        //assessment.addComment(comment1);
        assessment.setComments(comments);

        String id = assessmentService.save(assessment);

        assertEquals(Integer.valueOf(id), Integer.valueOf(assessment.getId()));
    }

    @Test
    public void aggregrateQuery() {
        // see ElasticsearchTemplateAggregationTests
        //QueryBuilder qb = QueryBuilders.queryString(searchTerm.getQueryString());
        String query = "*";
        //String anyQuery = "{" + "\"query_string\" : {\"query\" : " + query + " }}";  // not working
        QueryBuilder qb = QueryBuilders.queryString(query);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(qb)
            //.withQuery(matchAllQuery())
            .withSearchType(COUNT)
            .withIndices("vaindex").withTypes("assessment")
            .addAggregation(terms("phenotype").field("phenotype"))
            .build();

        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });

        assertThat(aggregations, is(notNullValue()));
        assertThat(aggregations.asMap().get("phenotype"), is(notNullValue()));

        Aggregation a = aggregations.asMap().get("phenotype");

        System.out.println("aggregrations: "  + a);
    }
}
