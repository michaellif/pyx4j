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
 * Created on 2010-09-09
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.commons;

/**
 * Exception that are safe to shown to user.
 */
public class UserRuntimeException extends RuntimeExceptionSerializable {

    private static final long serialVersionUID = 1L;

    private transient Throwable cause;

    private transient boolean skipLogStackTrace;

    protected UserRuntimeException() {
        super();
    }

    public UserRuntimeException(String message) {
        super(message);
    }

    public UserRuntimeException(boolean skipLogStackTrace, String message) {
        super(message);
        this.skipLogStackTrace = skipLogStackTrace;
    }

    public UserRuntimeException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    public boolean isSkipLogStackTrace() {
        return skipLogStackTrace;
    }
}
