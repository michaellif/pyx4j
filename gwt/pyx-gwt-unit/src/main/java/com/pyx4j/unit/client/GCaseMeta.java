/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 21, 2009
 * @author vlads
 * @version $Id: GCaseMeta.java 4436 2009-12-22 08:45:29Z vlads $
 */
package com.pyx4j.unit.client;

public interface GCaseMeta {

    public String getName();
    
    public void execute(GCaseResultAsyncCallback callback);
    
}
