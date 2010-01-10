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
 * Created on Apr 23, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.log4gwt.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LogEvent implements Serializable {

    private Level level;

    private long eventTime;

    /**
     * Formated Serializable Message
     */
    private String formatedMessage;

    transient private String message;

    transient private Throwable throwable;

    transient private Object data1;

    transient private Object data2;

    transient private Object[] dataArray;

    public LogEvent() {
    }

    public LogEvent(Level level, String message, Throwable throwable, Object data1) {
        super();
        this.eventTime = System.currentTimeMillis();
        this.level = level;
        this.message = message;
        this.data1 = data1;
        this.throwable = throwable;
    }

    public LogEvent(Level level, String message, Throwable throwable, Object data1, Object data2) {
        super();
        this.eventTime = System.currentTimeMillis();
        this.level = level;
        this.message = message;
        this.data1 = data1;
        this.data2 = data2;
        this.throwable = throwable;
    }

    public LogEvent(Level level, String message, Throwable throwable, Object... data) {
        super();
        this.eventTime = System.currentTimeMillis();
        this.level = level;
        this.message = message;
        this.dataArray = data;
        this.throwable = throwable;
    }

    public Level getLevel() {
        return level;
    }

    public long getEventTime() {
        return eventTime;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Object getData1() {
        return data1;
    }

    public Object getData2() {
        return data2;
    }

    public Object[] getDataArray() {
        return dataArray;
    }

    /**
     * Formated Serializable Message
     */
    public String getFormatedMessage() {
        return formatedMessage;
    }

    public void setFormatedMessage(String formatedMessage) {
        this.formatedMessage = formatedMessage;
    }
}
