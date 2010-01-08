/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Jan 7, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.criterion;

import java.io.Serializable;

import com.pyx4j.entity.shared.IObject;

@SuppressWarnings("serial")
public class PropertyCriterion implements Criterion {

    public static enum Restriction {
        LESS_THAN, LESS_THAN_OR_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL, EQUAL, NOT_EQUAL, IN
    }

    private String propertyName;

    private Restriction restriction;

    private Serializable value;

    protected PropertyCriterion() {

    }

    public PropertyCriterion(String propertyName, Restriction restriction, Serializable value) {
        this.propertyName = propertyName;
        this.restriction = restriction;
        this.value = value;
    }

    public static PropertyCriterion eq(IObject<?, ?> member, Serializable value) {
        return new PropertyCriterion(member.getFieldName(), Restriction.EQUAL, value);
    }

    public static PropertyCriterion eq(String propertyName, Serializable value) {
        return new PropertyCriterion(propertyName, Restriction.EQUAL, value);
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public Restriction getRestriction() {
        return this.restriction;
    }

    public Serializable getValue() {
        return this.value;
    }
}
