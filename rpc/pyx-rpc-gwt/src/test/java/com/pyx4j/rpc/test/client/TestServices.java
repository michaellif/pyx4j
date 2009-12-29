/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 29, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.test.client;

import com.pyx4j.rpc.shared.Service;

public interface TestServices {

    public interface Echo extends Service<String, String> {

    };

    public interface ThrowException extends Service<String, String> {

    };

}
