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
 * Created on Jul 3, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.security.server;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.memcache.StrictErrorHandler;

public class AppengineHelper {

    /**
     * Detects GAE Maintenance
     */
    public static boolean isMemcacheReadOnly() {
        MemcacheService ms = MemcacheServiceFactory.getMemcacheService();
        ms.setErrorHandler(new StrictErrorHandler());
        try {
            ms.put("test", System.currentTimeMillis());
        } catch (com.google.appengine.api.memcache.MemcacheServiceException e) {
            return true;
        }
        return false;
    }
}
