/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 21, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.client.impl;

import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import com.pyx4j.unit.client.GCaseMeta;


public interface GUnitMetaData {

    public Set<Class<? extends TestCase>> getAllCases();

    public List<GCaseMeta> getClassMeta(Class<? extends TestCase> gCaseClass);

}
