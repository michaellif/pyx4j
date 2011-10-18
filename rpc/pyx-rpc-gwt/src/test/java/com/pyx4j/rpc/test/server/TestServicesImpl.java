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
 * Created on Dec 29, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.test.server;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.RuntimeExceptionSerializable;
import com.pyx4j.rpc.test.client.TestServices;

public class TestServicesImpl {

    private static final Logger log = LoggerFactory.getLogger(TestServicesImpl.class);

    public static class EchoImpl implements TestServices.Echo {

        @Override
        public String execute(String request) {
            return request;
        }

    }

    public static class EchoSerializableImpl implements TestServices.EchoSerializable {

        @Override
        public Serializable execute(Serializable request) {
            log.debug("got {} will echo", request);
            return request;
        }
    }

    public static class ThrowExceptionImpl implements TestServices.ThrowException {

        @Override
        public String execute(String request) {
            if ((request == null) || (request.length() == 0)) {
                return request;
            } else {
                throw new RuntimeExceptionSerializable(request);
            }
        }

    }
}
