/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
