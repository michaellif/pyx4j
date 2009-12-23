/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 21, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.impl;

import java.util.List;
import java.util.Set;

import com.pyx4j.unit.GCaseMeta;

import junit.framework.TestCase;


public interface GUnitMetaData {

    public Set<Class<? extends TestCase>> getAllCases();

    public List<GCaseMeta> getClassMeta(Class<? extends TestCase> gCaseClass);

}
