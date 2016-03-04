/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Jan 26, 2016
 * @author vlads
 */
package com.pyx4j.commons;

import java.sql.Time;
import java.util.Date;

/**
 * Represent logical time (LocalTime) regardless of time zone. In GWT and on Server.
 *
 * This class has CustomFieldSerializer in module pyx-rpc-shared.
 *
 * Deprecation pending JRE 8 java.time support in GWT
 */
@SuppressWarnings("deprecation")
public class LogicalTime extends Time {

    private static final long serialVersionUID = 1L;

    public LogicalTime(int hour, int minute, int second) {
        super(hour, minute, second);
    }

    public LogicalTime(long time) {
        this(new Date(time));
    }

    public LogicalTime(Date date) {
        this(date.getHours(), date.getMinutes(), date.getSeconds());
    }

    @Override
    public int getHours() {
        return super.getHours();
    }

    @Override
    public void setHours(int hours) {
        super.setHours(hours);
    }

    @Override
    public int getMinutes() {
        return super.getMinutes();
    }

    @Override
    public void setMinutes(int minutes) {
        super.setMinutes(minutes);
    }

    @Override
    public int getSeconds() {
        return super.getSeconds();
    }

    @Override
    public void setSeconds(int seconds) {
        super.setSeconds(seconds);
    }

    public static LogicalTime valueOf(String time) {
        return new LogicalTime(Time.valueOf(time));
    }
}
