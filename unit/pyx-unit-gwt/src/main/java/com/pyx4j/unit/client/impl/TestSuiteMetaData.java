/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Apr 21, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.client.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import com.pyx4j.unit.client.GCaseMeta;

public interface TestSuiteMetaData {

    public Set<Class<? extends TestCase>> getAllCases();

    /**
     * Each List is assumed to be in one test class
     */
    public Collection<List<GCaseMeta>> getAllGCaseMeta();

    public List<GCaseMeta> getClassMeta(Class<? extends TestCase> gCaseClass);

}
