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
 * Created on Apr 21, 2009
 * @author vlads
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
