/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 29, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.test.server;

import com.pyx4j.rpc.test.client.TestServices.Echo;
import com.pyx4j.rpc.test.client.TestServices.ThrowException;

public class TestServicesImpl {

    public static class EchoImpl implements Echo {

        @Override
        public String execute(String request) {
            return request;
        }

    }

    public static class ThrowExceptionImpl implements ThrowException {

        @Override
        public String execute(String request) {
            if ((request == null) || (request.length() == 0)) {
                return request;
            } else {
                throw new RuntimeException(request);
            }
        }

    }
}
