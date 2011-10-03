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
 * Created on Feb 13, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.flex.folder;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeHandler;

public abstract class CEntityFolderItemEditor<E extends IEntity> extends CEntityEditor<E> {

    private final SimplePanel container;

    private boolean first;

    private final List<HandlerRegistration> handlerRegistrations;

    public CEntityFolderItemEditor(Class<E> clazz) {
        super(clazz);
        container = new SimplePanel();
        handlerRegistrations = new ArrayList<HandlerRegistration>();
    }

    @Override
    protected abstract IFolderItemDecorator<E> createDecorator();

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isFirst() {
        return first;
    }

    @Override
    public SimplePanel getContainer() {
        return container;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<E> handler) {
        HandlerRegistration handlerRegistration = super.addValueChangeHandler(handler);
        handlerRegistrations.add(handlerRegistration);
        return handlerRegistration;
    }

    @Override
    public HandlerRegistration addPropertyChangeHandler(PropertyChangeHandler handler) {
        HandlerRegistration handlerRegistration = super.addPropertyChangeHandler(handler);
        handlerRegistrations.add(handlerRegistration);
        return handlerRegistration;
    }

    public void removeAllHandlers() {
        for (HandlerRegistration handlerRegistration : handlerRegistrations) {
            handlerRegistration.removeHandler();
        }
    }

}
