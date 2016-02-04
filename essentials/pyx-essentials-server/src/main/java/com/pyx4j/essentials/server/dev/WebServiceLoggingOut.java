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
 * Created on Feb 2, 2016
 * @author vlads
 */
package com.pyx4j.essentials.server.dev;

import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.slf4j.Logger;

public class WebServiceLoggingOut extends LoggingOutInterceptor {

    private final Logger appLogger;

    public WebServiceLoggingOut(Logger appLogger) {
        super();
        this.appLogger = appLogger;
    }

    protected String secureLogMessage(String message) {
        return message.replaceAll("<ns(\\d):Password>.*</ns(\\d):Password>", "<ns$1:Password>***</ns$2:Password>") //
                .replaceAll("<Password>.*</Password>", "<Password>***</Password>");
    }

    @Override
    protected void log(java.util.logging.Logger logger, String message) {
        appLogger.debug(secureLogMessage(message));
    }

}
