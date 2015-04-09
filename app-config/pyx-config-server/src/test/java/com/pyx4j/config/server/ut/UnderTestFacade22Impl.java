/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jul 31, 2014
 * @author vlads
 */
package com.pyx4j.config.server.ut;

import com.pyx4j.config.server.Interceptors;

public class UnderTestFacade22Impl implements UnderTestFacade2 {

    @Override
    @Interceptors(UnderTestExceptionHandlerOnClass.class)
    public String echoOrThrowRedefinedOnClass(String value, Class<?> doThrow) {
        if (doThrow == ArithmeticException.class) {
            throw new ArithmeticException(value + "-.2");
        } else if (doThrow == IllegalMonitorStateException.class) {
            throw new IllegalMonitorStateException(value + "-.2");
        }
        return value;
    }
}
