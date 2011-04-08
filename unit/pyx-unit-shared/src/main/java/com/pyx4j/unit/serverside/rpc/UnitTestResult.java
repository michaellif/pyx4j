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
 * Created on Jan 4, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.serverside.rpc;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UnitTestResult implements Serializable {

    private long duration;

    private boolean success;

    private String exceptionClassName;

    private String exceptionMessage;

    public UnitTestResult() {
    }

    public UnitTestResult(String exceptionMessage) {
        this(false, exceptionMessage, 0);
    }

    public UnitTestResult(boolean success, String exceptionMessage, long duration) {
        this.success = success;
        this.duration = duration;
        this.exceptionMessage = exceptionMessage;
    }

    public UnitTestResult(Throwable exception, long duration) {
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

    public String getExceptionClassName() {
        return exceptionClassName;
    }

    public void setExceptionClassName(String exceptionClassName) {
        this.exceptionClassName = exceptionClassName;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
