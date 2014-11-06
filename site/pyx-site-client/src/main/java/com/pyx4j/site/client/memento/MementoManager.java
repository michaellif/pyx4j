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
 * Created on Nov 6, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.memento;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.pyx4j.security.client.BehaviorChangeEvent;
import com.pyx4j.security.client.BehaviorChangeHandler;
import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.site.rpc.AppPlace;

public class MementoManager {

    @SuppressWarnings("serial")
    private static final LinkedHashMap<AppPlace, Map<Object, Memento>> cache = new LinkedHashMap<AppPlace, Map<Object, Memento>>() {

        @Override
        protected boolean removeEldestEntry(Map.Entry<AppPlace, Map<Object, Memento>> eldest) {
            return size() > 20;
        }
    };

    static {
        ClientSecurityController.addSecurityControllerHandler(new BehaviorChangeHandler() {
            @Override
            public void onBehaviorChange(BehaviorChangeEvent event) {
                MementoManager.invalidate();
            }
        });
    }

    public static Memento retrieveMemento(AppPlace place, Object component) {
        if (cache.containsKey(place)) {
            Map<Object, Memento> mementos = cache.get(place);
            if (mementos.containsKey(component)) {
                return mementos.get(component);
            }
        }
        return null;
    }

    public static void storeMemento(Memento memento, AppPlace place, Object component) {
        assert memento != null;
        assert place != null;
        assert component != null;

        if (!cache.containsKey(place)) {
            cache.put(place, new HashMap<Object, Memento>());
        }
        cache.get(place).put(component, memento);
    }

    public static void invalidate() {
        cache.clear();
    }

}
