/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 23, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import junit.framework.TestCase;

import com.pyx4j.entity.test.env.ConfigureTestsEnv;

public abstract class InitializerTestCase extends TestCase {

    protected InitializerTestCase() {
        ConfigureTestsEnv.configure();
    }

    //TODO this is not implemented in GWT Unit
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ConfigureTestsEnv.configure();
    }
}
