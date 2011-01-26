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
 * Created on Dec 1, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.ria.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.event.shared.SimpleEventBus;

import com.pyx4j.ria.client.view.AbstractView;
import com.pyx4j.ria.client.view.ILayoutManager;
import com.pyx4j.ria.client.view.IPosition;
import com.pyx4j.ria.client.view.IViewManager;

public abstract class AbstractViewManager<T extends IPosition> implements IViewManager<T>, HasHandlers {

    private EventBus eventBus;

    private final ILayoutManager<T> layoutManager;

    public AbstractViewManager(ILayoutManager<T> layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void addView(AbstractView view) {
        T position = getPositionForView(view.getClass());
        layoutManager.getFolder(position).addView(view);
    }

    @Override
    public void showView(AbstractView view) {
        T position = getPositionForView(view.getClass());
        layoutManager.getFolder(position).showView(view);
    }

    @Override
    public void addAndShowView(AbstractView view) {
        T position = getPositionForView(view.getClass());
        layoutManager.getFolder(position).addView(view);
        layoutManager.getFolder(position).showView(view);
    }

    @Override
    public void closeView(AbstractView view) {
        T position = getPositionForView(view.getClass());
        layoutManager.getFolder(position).removeView(view);
    }

    //    abstract void showEntity(IEntity entity);
    //
    //    abstract void showEntityList(EntitySearchCriteria<?> criteria);

    @Override
    public void fireEvent(GwtEvent<?> event) {
        if (eventBus != null) {
            eventBus.fireEventFromSource(event, this);
        }
    }

    protected EventBus ensureHandlers() {
        return eventBus == null ? eventBus = new SimpleEventBus() : eventBus;
    }

    protected final <H extends EventHandler> HandlerRegistration addHandler(final H handler, GwtEvent.Type<H> type) {
        return ensureHandlers().addHandler(type, handler);
    }

}
