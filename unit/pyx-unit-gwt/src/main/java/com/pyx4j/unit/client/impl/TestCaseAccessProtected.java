/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Dec 30, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.client.impl;

/**
 * 
 * TestCase used in GWT mode has this interface implemented, so we are able to call
 * protected setUp() and tearDown().
 * 
 */
public interface TestCaseAccessProtected {

    public void accessProtectedSetUp() throws Exception;

    public void accessProtectedTearDown() throws Exception;

}
