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
 * Created on Jul 23, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client;

import java.util.HashMap;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.IsWidget;

public class SingletonViewFactory implements ViewFactory {

    private final HashMap<Class<?>, IsWidget> map = new HashMap<Class<?>, IsWidget>();

    private static ViewFactory factory = GWT.create(ViewFactory.class);

    @Override
    public <T extends IsView> T instantiate(Class<T> type) {
        if (!map.containsKey(type)) {
            map.put(type, factory.instantiate(type));
        }

        @SuppressWarnings("unchecked")
        T impl = (T) map.get(type);
        if (impl == null) {
            throw new Error("implementation of " + type.getName() + " not found");
        }
        return impl;
    }

    public void invalidate() {
        map.clear();
    }

}
