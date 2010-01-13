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
 * Created on Jan 13, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.security.server;

import com.pyx4j.security.shared.SecurityController;

/**
 * Use reflection to work on server side.
 * 
 * N.B. This class in replaced in GWT mode using 'super-source'
 */
public final class SecurityControllerCreator {

    private static final String SERVER_SIDE_SECURITY_CONTROLLER = "com.pyx4j.security.server.SessionBaseSecurityController";

    @SuppressWarnings("unchecked")
    public static final SecurityController createSecurityController() {
        try {
            Class<SecurityController> klass = (Class<SecurityController>) Class.forName(SERVER_SIDE_SECURITY_CONTROLLER);
            return klass.newInstance();
        } catch (Throwable e) {
            throw new RuntimeException("Can't create " + SERVER_SIDE_SECURITY_CONTROLLER, e);
        }
    }
}
