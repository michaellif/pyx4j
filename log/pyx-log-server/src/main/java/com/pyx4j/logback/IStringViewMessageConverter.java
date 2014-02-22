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
 * Created on Oct 29, 2013
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.logback;

import org.slf4j.helpers.MessageFormatter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.LoggingEventAccess;

import com.pyx4j.commons.IStringView;

public class IStringViewMessageConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        Object[] argumentArray = event.getArgumentArray();
        if (argumentArray != null) {
            boolean hasIStringView = false;
            for (int i = 0; i < argumentArray.length; i++) {
                if (argumentArray[i] instanceof IStringView) {
                    argumentArray[i] = ((IStringView) argumentArray[i]).getStringView();
                    hasIStringView = true;
                }
            }
            if (hasIStringView) {
                LoggingEventAccess.setFormattedMessage((LoggingEvent) event, MessageFormatter.arrayFormat(event.getMessage(), argumentArray).getMessage());
            }
        }
        return event.getFormattedMessage();
    }

}
