/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Apr 21, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.client;

public interface GCaseMeta {

    public String getTestClassName();

    public String getTestName();

    public void execute(GCaseResultAsyncCallback callback);

}
