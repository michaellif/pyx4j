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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.events.PropertyChangeHandler;

public abstract class CEntityFolderItemEditor<E extends IEntity> extends CEntityEditor<E> {

    private final SimplePanel container;

    private boolean first;

    private boolean last;

    private boolean moovable = true;

    private boolean remoovable = true;

    private final List<HandlerRegistration> handlerRegistrations;

    public CEntityFolderItemEditor(Class<E> clazz) {
        this(clazz, true, true);
    }

    public CEntityFolderItemEditor(Class<E> clazz, boolean moovable, boolean remoovable) {
        super(clazz);
        container = new SimplePanel();
        this.moovable = moovable;
        this.remoovable = remoovable;

        handlerRegistrations = new ArrayList<HandlerRegistration>();
    }

    @Override
    protected abstract IFolderItemDecorator<E> createDecorator();

    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }

    public boolean isMoovable() {
        return moovable;
    }

    public boolean isRemoovable() {
        return remoovable;
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

    private HandlerRegistration addItemRemoveClickHandler(ClickHandler handler) {
        if (getDecorator() instanceof IFolderItemDecorator) {
            HandlerRegistration handlerRegistration = ((IFolderItemDecorator<?>) getDecorator()).addItemRemoveClickHandler(handler);
            handlerRegistrations.add(handlerRegistration);
            return handlerRegistration;
        } else {
            return null;
        }
    }

    private HandlerRegistration addRowUpClickHandler(ClickHandler handler) {
        if (getDecorator() instanceof IFolderItemDecorator) {
            HandlerRegistration handlerRegistration = ((IFolderItemDecorator<?>) getDecorator()).addRowUpClickHandler(handler);
            handlerRegistrations.add(handlerRegistration);
            return handlerRegistration;
        } else {
            return null;
        }
    }

    private HandlerRegistration addRowDownClickHandler(ClickHandler handler) {
        if (getDecorator() instanceof IFolderItemDecorator) {
            HandlerRegistration handlerRegistration = ((IFolderItemDecorator<?>) getDecorator()).addRowDownClickHandler(handler);
            handlerRegistrations.add(handlerRegistration);
            return handlerRegistration;
        } else {
            return null;
        }
    }

    protected void onAbandon() {
        for (HandlerRegistration handlerRegistration : handlerRegistrations) {
            handlerRegistration.removeHandler();
        }
    }

    protected void onAdopt(final CEntityFolder<E> parent) {

        HandlerRegistration handlerRegistration = parent.addValueChangeHandler(new ValueChangeHandler<IList<E>>() {

            @Override
            public void onValueChange(ValueChangeEvent<IList<E>> event) {
                setActionsState(parent);
            }
        });
        handlerRegistrations.add(handlerRegistration);

        addItemRemoveClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                parent.removeItem(CEntityFolderItemEditor.this);
            }
        });
        addRowUpClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                parent.moveUpItem(CEntityFolderItemEditor.this);
            }
        });
        addRowDownClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                parent.moveDownItem(CEntityFolderItemEditor.this);
            }
        });

    }

    protected void setActionsState(CEntityFolder<E> parent) {
        int index = parent.getItemIndex(CEntityFolderItemEditor.this);

        first = index == 0;
        last = index == parent.getItemCount() - 1;

        CEntityFolderItemEditor<?> previousSibling = parent.getItem(index - 1);
        CEntityFolderItemEditor<?> nextSibling = parent.getItem(index + 1);

        ((IFolderItemDecorator<?>) getDecorator()).setActionsState(remoovable, !first && previousSibling.isMoovable(), !last && nextSibling.isMoovable());

    }

}
