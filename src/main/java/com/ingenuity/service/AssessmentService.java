package com.ingenuity.service;

import com.ingenuity.model.VariantAssessment;

/**
 * Created by IntelliJ IDEA.
 * User: pschmidt
 * Date: 7/1/15
 * Time: 4:42 PM
 * <p>
 * Copyright (C) 2015 Ingenuity Systems, Inc. All rights reserved.
 * <p>
 * This software is the confidential & proprietary information of Ingenuity Systems, Inc.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of any agreement or agreements you entered into with
 * Ingenuity Systems.
 */
public interface AssessmentService {
    String save(VariantAssessment assessment);
    VariantAssessment findOne(String id);
}
