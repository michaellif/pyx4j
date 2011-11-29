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
 * Created on Nov 29, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.server.contexts;

public class UserAgentInfo {

    public static final UserAgenDetection get() {
        UserAgenDetection d = (UserAgenDetection) Context.getRequest().getAttribute(UserAgenDetection.class.getName());
        if (d == null) {
            d = new UserAgenDetection(Context.getRequestHeader("User-Agent"));
            Context.getRequest().setAttribute(UserAgenDetection.class.getName(), d);
        }
        return d;
    }

    public static class UserAgenDetection {

        private boolean appleWebKit;

        private boolean iPad;

        private boolean mobile;

        protected UserAgenDetection(String userAgent) {
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

}
