/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Sep 28, 2009
 * @author vlads
 * @version $Id: TestAwareExceptionHandler.java 4436 2009-12-22 08:45:29Z vlads $
 */
package com.pyx4j.unit.client;

import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;

public interface TestAwareExceptionHandler {

    public void delegateExceptionHandler(UncaughtExceptionHandler testHandler);

}
