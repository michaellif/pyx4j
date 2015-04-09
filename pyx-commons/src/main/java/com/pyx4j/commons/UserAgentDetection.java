/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on May 16, 2013
 * @author Mykola
 */
package com.pyx4j.commons;

public class UserAgentDetection {

    private boolean appleWebKit;

    private boolean iPad;

    private boolean mobile;

    public UserAgentDetection(String userAgent) {
        if (userAgent != null) {
            appleWebKit = userAgent.contains("AppleWebKit");
            mobile = userAgent.contains("Mobile");
            if (appleWebKit) {
                iPad = userAgent.contains("iPad");
            }
        }
    }

    public final boolean isIPad() {
        return iPad;
    }

    public final boolean isWebKit() {
        return appleWebKit;
    }

    public final boolean isMobile() {
        return mobile;
    }

}
