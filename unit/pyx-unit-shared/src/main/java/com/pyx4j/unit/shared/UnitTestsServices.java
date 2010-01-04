/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 4, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.shared;

import java.util.Vector;

import com.pyx4j.rpc.shared.Service;

public interface UnitTestsServices {

    public interface GetTestsList extends Service<String, Vector<UnitTestInfo>> {

    };

    public interface ExectuteTest extends Service<UnitTestExecuteRequest, UnitTestResult> {

    };
}
