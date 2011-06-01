/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2011-06-01
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.commons;

import java.util.Date;

/**
 * Represent logical date regardless of time zone. In GWT and on Server.
 * 
 * This class has CustomFieldSerializer in module pyx-rpc-shared.
 */
public class LogicalDate extends java.sql.Date {

    private static final long serialVersionUID = -392497247369233325L;

    @SuppressWarnings("deprecation")
    public LogicalDate() {
        super(0);
        Date now = new Date();
        this.setTime(new Date(now.getYear(), now.getMonth(), now.getDate()).getTime());
    }

    @SuppressWarnings("deprecation")
    public LogicalDate(int year, int month, int day) {
        super(year, month, day);
    }

    public LogicalDate(long date) {
        this(new Date(date));
    }

    @SuppressWarnings("deprecation")
    public LogicalDate(Date date) {
        super(0);
        this.setTime(new Date(date.getYear(), date.getMonth(), date.getDate()).getTime());
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getYear() {
        return super.getYear();
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getMonth() {
        return super.getMonth();
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getDate() {
        return super.getDate();
    }
}
