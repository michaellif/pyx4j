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
 * Created on Feb 21, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TryClassicConverter1 {

    public static void main(String[] args) {
        System.setProperty("logback.configurationFile", "./src/test/resources/logback-config1.xml");

        Logger log = LoggerFactory.getLogger(TryClassicConverter1.class);
        log.info("test message: {}", new DebugObject("print this"));
    }
}
