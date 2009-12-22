/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 21, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit;

public interface GCaseMeta {

    public String getName();
    
    public void execute(GCaseResultAsyncCallback callback);
    
}
