/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2014
 * @author vlads
 */
package com.propertyvista.server.config.logback;

import org.apache.commons.lang.time.FastDateFormat;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class LogstashFilter extends Filter<ILoggingEvent> {

    private static final FastDateFormat DATETIME_TIMEFORMAT_WITH_MILLIS = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public FilterReply decide(ILoggingEvent event) {
        event.getMDCPropertyMap().put("localtime", DATETIME_TIMEFORMAT_WITH_MILLIS.format(event.getTimeStamp()));
        return FilterReply.ACCEPT;
    }
}
