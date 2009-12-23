/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 22, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.client;

public class GResult {

    private long duration;
    
    private boolean success;
    
    private String exceptionMessage;

    /**
     * @param duration
     * @param success
     * @param exceptionMessage
     */
    public GResult(boolean success, String exceptionMessage, long duration) {
        super();
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

    public String getMessage() {
        return exceptionMessage;
    }
}
