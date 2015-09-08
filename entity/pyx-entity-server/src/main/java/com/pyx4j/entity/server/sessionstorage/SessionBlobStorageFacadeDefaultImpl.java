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
 * Created on Sep 7, 2015
 * @author vlads
 */
package com.pyx4j.entity.server.sessionstorage;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.server.contexts.ServerContext;

public class SessionBlobStorageFacadeDefaultImpl implements SessionBlobStorageFacade {

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Serializable> getStorage(String storageType) {
        final String attributeName = "ss_" + storageType;

        Map<String, Serializable> map;
        synchronized (SessionBlobStorageFacadeDefaultImpl.class) {
            map = (Map<String, Serializable>) ServerContext.getVisit().getAttribute(attributeName);
            if (map == null) {
                map = Collections.synchronizedMap(new HashMap<String, Serializable>());
                ServerContext.getVisit().setAttribute(attributeName, (Serializable) map);
            }
        }

        final ObservableMapImpl<String, Serializable> om = new ObservableMapImpl<String, Serializable>(map);
        om.addListener(new ObservableMapChangeListener() {
            @Override
            public void onChanged() {
                ServerContext.getVisit().setAttribute(attributeName, (Serializable) om.delegate());
            }
        });

        return om;
    }

}
