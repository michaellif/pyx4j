/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 22, 2009
 * @author vlads
 * @version $Id: AbstractGUnitMetaData.java 4436 2009-12-22 08:45:29Z vlads $
 */
package com.pyx4j.unit.client.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.pyx4j.unit.client.GCaseMeta;

import junit.framework.TestCase;


public abstract class AbstractGUnitMetaData implements GUnitMetaData {

    private final Map<Class<? extends TestCase>, List<GCaseMeta>> metaMap;

    protected AbstractGUnitMetaData() {
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

    public Set<Class<? extends TestCase>> getAllCases() {
        return metaMap.keySet();
    }

    public List<GCaseMeta> getClassMeta(Class<? extends TestCase> caseClass) {
        return metaMap.get(caseClass);
    }

}
