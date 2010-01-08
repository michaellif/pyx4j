/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Jan 4, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UnitTestResult implements Serializable {

    private long duration;

    private boolean success;

    private String exceptionMessage;

    public UnitTestResult() {
    }

    public UnitTestResult(String exceptionMessage) {
        this(false, exceptionMessage, 0);
    }

    public UnitTestResult(boolean success, String exceptionMessage, long duration) {
        this.duration = duration;
        this.success = success;
        this.exceptionMessage = exceptionMessage;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isSuccess() {
        return success;
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
