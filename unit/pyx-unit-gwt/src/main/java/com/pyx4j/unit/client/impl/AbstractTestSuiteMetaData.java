/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Apr 22, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.client.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import junit.framework.TestCase;

import com.pyx4j.unit.client.GCaseMeta;

public abstract class AbstractTestSuiteMetaData implements TestSuiteMetaData {

    private final Map<Class<? extends TestCase>, List<GCaseMeta>> metaMap;

    protected AbstractTestSuiteMetaData() {
        metaMap = new HashMap<Class<? extends TestCase>, List<GCaseMeta>>();
    }

    protected void addCase(Class<? extends TestCase> caseClass, AbstractGCaseMeta caseMeta) {
        List<GCaseMeta> l = metaMap.get(caseClass);
        if (l == null) {
            l = new Vector<GCaseMeta>();
            metaMap.put(caseClass, l);
        }
        l.add(caseMeta);
    }

    @Override
    public Set<Class<? extends TestCase>> getAllCases() {
        return metaMap.keySet();
    }

    @Override
    public Collection<List<GCaseMeta>> getAllGCaseMeta() {
        return metaMap.values();
    }

    @Override
    public List<GCaseMeta> getClassMeta(Class<? extends TestCase> caseClass) {
        return metaMap.get(caseClass);
    }

}
