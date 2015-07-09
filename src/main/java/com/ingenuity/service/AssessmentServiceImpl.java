package com.ingenuity.service;

import com.ingenuity.model.VariantAssessment;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.facet.FacetBuilders;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * User: pschmidt
 * Date: 7/1/15
 * Time: 4:43 PM
 */
@Service
public class AssessmentServiceImpl implements AssessmentService {
    @Autowired
    private ElasticsearchTemplate template;


    /*
    public AssessmentServiceImpl(ElasticsearchTemplate template)  {
        this.template = template;
    }
    */

    @Override
    public String save(VariantAssessment assessment) {
        IndexQuery query = new IndexQueryBuilder().withIndexName("vaindex").withId(String.valueOf(assessment.getId())).withType("assessment").withObject(assessment).build();

        String resultId = template.index(query);

        return resultId;
    }

    @Override
    public VariantAssessment findOne(String id) {
        GetQuery getQuery = new GetQuery();
        getQuery.setId(id);

        VariantAssessment queriedObject = template.queryForObject(getQuery, VariantAssessment.class);

        return queriedObject;
    }

    public String getJsonFreeformed(String query) {
        return null;
    }
}
