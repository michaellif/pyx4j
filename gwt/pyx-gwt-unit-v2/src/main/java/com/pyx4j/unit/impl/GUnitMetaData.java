/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 21, 2009
 * @author vlads
 * @version $Id: GUnitMetaData.java 4436 2009-12-22 08:45:29Z vlads $
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
