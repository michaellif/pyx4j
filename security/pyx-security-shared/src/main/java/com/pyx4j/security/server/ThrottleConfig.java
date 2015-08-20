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
 * Created on 2010-07-23
 * @author vlads
 */
package com.pyx4j.security.server;

import java.util.Collection;
import java.util.Collections;

import com.pyx4j.commons.Consts;

/**
 * Default Anti-DoS feature configuration
 */
public class ThrottleConfig {

    public boolean isEnabled() {
        return true;
    }

    /**
     * Monitoring interval in milliseconds.
     */
    public long getInterval() {
        return 3 * Consts.MIN2MSEC;
    }

    public long getMaxTimeUsage() {
        return Consts.MIN2MSEC;
    }

    public long getMaxRequests() {
        return 60;
    }

    /**
     * First two levels of RequestURI that are not counted for DoS.
     * 
     * Example: "/_ah/admin"
     */
    public Collection<String> whiteRequestURIs() {
        return Collections.emptyList();
    }
}
