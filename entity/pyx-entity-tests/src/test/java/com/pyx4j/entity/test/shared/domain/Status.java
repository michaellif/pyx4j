/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 29, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared.domain;

public enum Status {

    ACTIVE("Active"),

    DEACTIVATED("Deactivated"),

    SUSPENDED("Suspended");

    private String descr;

    private Status(String descr) {
        this.descr = descr;
    }

    @Override
    public String toString() {
        return descr;
    }
}
