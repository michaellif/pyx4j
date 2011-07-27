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
 * Created on Feb 11, 2011
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.flex.editor;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.client.ui.flex.CEntityComponent;
import com.pyx4j.entity.client.ui.flex.CEntityContainer;
import com.pyx4j.entity.client.ui.flex.NativeEntityPanel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.ValidationResults;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

/**
 * This component represents list of IEntities
 */
public abstract class CEntityFolderEditor<E extends IEntity> extends CEntityContainer<IList<E>, NativeEntityPanel<IList<E>>> {

    private static final Logger log = LoggerFactory.getLogger(CEntityFolderEditor.class);

    private IFolderEditorDecorator<E> folderDecorator;

    private final FlowPanel container;

    protected int currentRowDebugId = 0;

    private final LinkedHashMap<E, CEntityFolderItemEditor<E>> itemsMap;

    private final E entityPrototype;

    private boolean externalAddItemProcessing = false;

    public CEntityFolderEditor(Class<E> rowClass) {
        container = new FlowPanel();
        itemsMap = new LinkedHashMap<E, CEntityFolderItemEditor<E>>();
        if (rowClass != null) {
            entityPrototype = EntityFactory.getEntityPrototype(rowClass);
        } else {
            entityPrototype = null;
        }
    }

    /**
     * This mainly use for columns creation when TableFolderDecorator is used
     */
    public E proto() {
        return entityPrototype;
    }

    @Override
    public void onBound(CEntityComponent<?, ?> parent) {
        super.onBound(parent);
        setFolderDecorator(createContent());
        addValidations();
    }

    @Override
    public IFolderEditorDecorator<E> createContent() {
        return createFolderDecorator();
    }

    protected abstract CEntityFolderItemEditor<E> createItem();

    private CEntityFolderItemEditor<E> createItemPrivate() {

        CEntityFolderItemEditor<E> item = createItem();

        item.addValueChangeHandler(new ValueChangeHandler<E>() {
            boolean sheduled = false;

            @Override
            public void onValueChange(final ValueChangeEvent<E> event) {
                if (!sheduled) {
                    sheduled = true;
                    Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            log.debug("CEntityFolder.onValueChange fired from {}. New value is {}.", CEntityFolderEditor.this.getTitle(), event.getValue());
                            revalidate();
                            ValueChangeEvent.fire(CEntityFolderEditor.this, getValue());
                            sheduled = false;
                        }
                    });
                }

            }
        });

        item.addPropertyChangeHandler(new PropertyChangeHandler() {
            boolean sheduled = false;

            @Override
            public void onPropertyChange(final PropertyChangeEvent event) {
                sheduled = true;
                if (!sheduled) {
                    Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            log.debug("CEntityFolder.onPropertyChange fired from {}. Changed property is {}.", CEntityFolderEditor.this.getTitle(),
                                    event.getPropertyName());
                            revalidate();
                            ValueChangeEvent.fire(CEntityFolderEditor.this, getValue());
                            sheduled = false;
                        }
                    });
                }
            }
        });

        return item;
    }

    protected abstract IFolderEditorDecorator<E> createFolderDecorator();

    public void setExternalAddItemProcessing(boolean externalAddItemProcessing) {
        this.externalAddItemProcessing = externalAddItemProcessing;
    }

    public void setFolderDecorator(IFolderEditorDecorator<E> folderDecorator) {
        this.folderDecorator = folderDecorator;

        addValueChangeHandler(folderDecorator);

        asWidget().setWidget(folderDecorator);

        folderDecorator.setFolder(this);

        if (!externalAddItemProcessing) {
            folderDecorator.addItemAddClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    addItem();
                }
            });
        }
        //TODO use components inheritance
        if (this.getDebugId() != null) {
            folderDecorator.asWidget().ensureDebugId(this.getDebugId().debugId() + IFolderEditorDecorator.DEBUGID_SUFIX);
        }
    }

    public IFolderEditorDecorator<E> getFolderDecorator() {
        return folderDecorator;
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        super.setDebugId(debugId);
        if ((debugId != null) && (folderDecorator != null)) {
            folderDecorator.asWidget().ensureDebugId(this.getDebugId().debugId() + IFolderEditorDecorator.DEBUGID_SUFIX);
        }
    }

    @SuppressWarnings("unchecked")
    protected void addItem() {
        addItem((E) EntityFactory.create(entityPrototype.getValueClass()));
    }

    protected void addItem(E newEntity) {
        if (getValue() == null) {
            log.warn("Request to add item has been issued before the form populated with value");
            return;
        }

        final CEntityFolderItemEditor<E> comp = createItemPrivate();
        createNewEntity(newEntity, new DefaultAsyncCallback<E>() {
            @Override
            public void onSuccess(E result) {
                comp.setFirst(container.getWidgetCount() == 0);
                getValue().add(result);
                comp.onBound(CEntityFolderEditor.this);
                comp.populate(result);
                adoptFolderItem(comp);
                ValueChangeEvent.fire(CEntityFolderEditor.this, getValue());
            }

        });

    }

    protected void removeItem(CEntityFolderItemEditor<E> item, IFolderItemEditorDecorator<E> folderItemDecorator) {
        getValue().remove(item.getValue());
        abandonFolderItem(item);
        item.removeAllHandlers();
        ValueChangeEvent.fire(CEntityFolderEditor.this, getValue());
    }

    /**
     * Implementation to override new Entity creation. No need to call
     * super.createNewEntity().
     * 
     * @param newEntity
     * @param callback
     */
    protected void createNewEntity(E newEntity, AsyncCallback<E> callback) {
        callback.onSuccess(newEntity);
    }

    @Override
    public void setValue(IList<E> value) {
        super.setValue(value);
        repopulate(value);
    }

    @Override
    public void populate(IList<E> value) {
        super.populate(value);
        repopulate(value);
    }

    protected void repopulate(IList<E> value) {
        HashMap<E, CEntityFolderItemEditor<E>> oldMap = new HashMap<E, CEntityFolderItemEditor<E>>(itemsMap);

        itemsMap.clear();
        currentRowDebugId = 0;

        boolean first = true;
        for (E item : value) {
            CEntityFolderItemEditor<E> comp = null;
            if (oldMap.containsKey(item)) {
                comp = oldMap.remove(item);
                comp.setFirst(first);
            } else {
                comp = createItemPrivate();
                //Call setFirst before onBound()
                comp.setFirst(first);
                comp.onBound(this);
            }
            first = false;
            comp.populate(item);
            adoptFolderItem(comp);
        }

        for (CEntityFolderItemEditor<E> item : oldMap.values()) {
            container.remove(item);
        }

        if (folderDecorator instanceof TableFolderEditorDecorator) {
            ((TableFolderEditorDecorator<E>) folderDecorator).setHeaderVisible(container.getWidgetCount() > 0);
        }

    }

    private void abandonFolderItem(final CEntityFolderItemEditor<E> component) {
        container.remove(component);
        itemsMap.remove(component.getValue());
        ValueChangeEvent.fire(this, getValue());
    }

    private void adoptFolderItem(final CEntityFolderItemEditor<E> component) {

        final IFolderItemEditorDecorator<E> folderItemDecorator = component.createFolderItemDecorator();

        component.setFolderItemDecorator(folderItemDecorator);
        component.addAccessAdapter(this);
        if (container.getWidgetIndex(component) == -1) {
            container.add(component);
        }
        itemsMap.put(component.getValue(), component);

        IDebugId rowDebugId = new CompositeDebugId(this.getDebugId(), "row", currentRowDebugId);
        currentRowDebugId++;

        component.setDebugId(rowDebugId);
        folderItemDecorator.asWidget().ensureDebugId(rowDebugId.debugId());

        folderItemDecorator.addItemRemoveClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                removeItem(component, folderItemDecorator);
            }
        });

    }

    @Override
    public Collection<? extends CEditableComponent<?, ?>> getComponents() {
        if (itemsMap != null) {
            return itemsMap.values();
        } else {
            return null;
        }
    }

    @Override
    public ValidationResults getValidationResults() {
        return getAllValidationResults();
    }

    protected CEntityFolderItemEditor<E> getFolderRow(E value) {
        return itemsMap.get(value);
    }

    @Override
    protected NativeEntityPanel<IList<E>> createWidget() {
        return new NativeEntityPanel<IList<E>>();
    }

    public FlowPanel getContainer() {
        return container;
    }

    @Override
    public void applyVisibilityRules() {
        super.applyVisibilityRules();
        if (getComponents() != null) {
            for (CComponent<?> component : getComponents()) {
                component.applyVisibilityRules();
            }
        }
    }

    @Override
    public void applyEnablingRules() {
        super.applyEnablingRules();
        if (getComponents() != null) {
            for (CComponent<?> component : getComponents()) {
                component.applyEnablingRules();
            }
        }
    }

    @Override
    public void applyEditabilityRules() {
        if (getComponents() != null) {
            for (CComponent<?> component : getComponents()) {
                if (component instanceof CEditableComponent<?, ?>) {
                    ((CEditableComponent<?, ?>) component).applyEditabilityRules();
                }
            }
        }
    }
}
