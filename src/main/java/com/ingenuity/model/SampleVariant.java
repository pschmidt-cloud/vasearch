package com.ingenuity.model;

/*
 * Copyright (C) 2015 Ingenuity Systems, Inc. All rights reserved.
 *
 * This software is the confidential & proprietary information of Ingenuity Systems, Inc.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of any agreement or agreements you entered into with Ingenuity Systems.
 */
import java.io.Serializable;

public class SampleVariant extends SampleBase implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String chromosome;
    protected int position;
    protected String reference;
    protected String alternate;

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getAlternate() {
        return alternate;
    }

    public void setAlternate(String alternate) {
        this.alternate = alternate;
    }
}
