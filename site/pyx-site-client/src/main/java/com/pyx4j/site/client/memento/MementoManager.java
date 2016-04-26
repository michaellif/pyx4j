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
 */
package com.pyx4j.site.client.memento;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.security.client.BehaviorChangeEvent;
import com.pyx4j.security.client.BehaviorChangeHandler;
import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.memento.IMementoAware;

public class MementoManager {

    @SuppressWarnings("serial")
    private static final LinkedHashMap<AppPlace, Map<IsWidget, List<?>>> cache = new LinkedHashMap<AppPlace, Map<IsWidget, List<?>>>() {

        @Override
        protected boolean removeEldestEntry(Map.Entry<AppPlace, Map<IsWidget, List<?>>> eldest) {
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

    public static void saveState(IsWidget widget, AppPlace place) {
        if (widget instanceof IMementoAware) {
            SiteMementoInput mementoInput = new SiteMementoInput();
            ((IMementoAware) widget).saveState(mementoInput);
            storeMemento(mementoInput.getState(), place, widget);
        }
        if (widget.asWidget() instanceof HasWidgets) {
            for (Iterator<Widget> iterator = ((HasWidgets) widget.asWidget()).iterator(); iterator.hasNext();) {
                saveState(iterator.next(), place);
            }
        }
    }

    public static void restoreState(IsWidget widget, AppPlace place) {
        if (widget instanceof IMementoAware) {
            List<?> state = retrieveMemento(place, widget);
            SiteMementoOutput mementoOutput = new SiteMementoOutput(state);
            ((IMementoAware) widget).restoreState(mementoOutput);
        }
        if (widget.asWidget() instanceof HasWidgets) {
            for (Iterator<Widget> iterator = ((HasWidgets) widget.asWidget()).iterator(); iterator.hasNext();) {
                restoreState(iterator.next(), place);
            }
        }
    }

    private static void storeMemento(List<?> memento, AppPlace place, IsWidget widget) {
        assert memento != null;
        assert place != null;
        assert widget != null;

        if (!cache.containsKey(place)) {
            cache.put(place, new HashMap<IsWidget, List<?>>());
        }
        cache.get(place).put(widget, memento);
    }

    private static List<?> retrieveMemento(AppPlace place, IsWidget widget) {
        if (cache.containsKey(place)) {
            Map<IsWidget, List<?>> mementos = cache.get(place);
            if (mementos.containsKey(widget)) {
                return mementos.get(widget);
            }
        }
        return null;
    }

    private static void invalidate() {
        cache.clear();
    }

}
