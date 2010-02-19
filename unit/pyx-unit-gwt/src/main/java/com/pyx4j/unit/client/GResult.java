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
 * Created on Apr 22, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.client;

public class GResult {

    private final long duration;

    private final boolean success;

    private final String exceptionClassName;

    private final String exceptionMessage;

    public GResult(long duration) {
        this.duration = duration;
        this.success = true;
        this.exceptionClassName = null;
        this.exceptionMessage = null;
    }

    public GResult(boolean success, String exceptionClassName, String exceptionMessage, long duration) {
        this.duration = duration;
        this.success = success;
        this.exceptionClassName = exceptionClassName;
        this.exceptionMessage = exceptionMessage;
    }

    public GResult(Throwable exception, long duration) {
        this.success = false;
        this.duration = duration;
        this.exceptionClassName = exception.getClass().getName();
        this.exceptionMessage = exception.getMessage();
    }

    public long getDuration() {
        return duration;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        if (getExceptionMessage() != null) {
            return getExceptionClassName() + " [" + getExceptionMessage() + "]";
        } else {
            return getExceptionClassName();
        }
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public String getExceptionClassName() {
        return exceptionClassName;
    }
}
