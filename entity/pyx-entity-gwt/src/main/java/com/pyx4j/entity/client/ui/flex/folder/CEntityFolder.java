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
package com.pyx4j.entity.client.ui.flex.folder;

import java.util.Collection;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.client.ui.flex.CEntityComponent;
import com.pyx4j.entity.client.ui.flex.CEntityContainer;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.ValidationResults;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleName;

/**
 * This component represents list of IEntities
 */
public abstract class CEntityFolder<E extends IEntity> extends CEntityContainer<IList<E>> {

    private static final Logger log = LoggerFactory.getLogger(CEntityFolder.class);

    public static enum StyleName implements IStyleName {
        EntityFolder, EntityFolderActionsBar, EntityFolderAddButton, EntityFolderAddButtonImage, EntityFolderAddButtonLabel,

        //Box
        EntityFolderBoxItem, EntityFolderBoxDecorator, EntityFolderBoxItemDecorator,

        //Table
        EntityFolderRowItem, EntityFolderTableDecorator, EntityFolderRowItemDecorator
    }

    public static enum StyleDependent implements IStyleDependent {
        hover
    }

    private IFolderDecorator<E> folderDecorator;

    private final FlowPanel container;

    protected int currentRowDebugId = 0;

    private final HashMap<E, CEntityFolderItemEditor<E>> itemsMap;

    private final E entityPrototype;

    public CEntityFolder(Class<E> rowClass) {
        container = new FlowPanel();
        asWidget().setStyleName(StyleName.EntityFolder.name());
        itemsMap = new HashMap<E, CEntityFolderItemEditor<E>>();
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
        initContent();
        addValidations();
    }

    @Override
    public IsWidget createContent() {
        // TODO Auto-generated method stub
        return null;
    }

    protected abstract CEntityFolderItemEditor<E> createItem();

    private CEntityFolderItemEditor<E> createItemPrivate(boolean first) {

        CEntityFolderItemEditor<E> item = createItem();

        //Call setFirst before onBound()
        item.setFirst(first);
        item.onBound(this);

        item.addValueChangeHandler(new ValueChangeHandler<E>() {
            boolean sheduled = false;

            @Override
            public void onValueChange(final ValueChangeEvent<E> event) {
                if (!sheduled) {
                    sheduled = true;
                    Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            log.debug("CEntityFolder.onValueChange fired from {}. New value is {}.", CEntityFolder.this.getTitle(), event.getValue());
                            revalidate();
                            ValueChangeEvent.fire(CEntityFolder.this, getValue());
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
                            log.debug("CEntityFolder.onPropertyChange fired from {}. Changed property is {}.", CEntityFolder.this.getTitle(),
                                    event.getPropertyName());
                            revalidate();
                            ValueChangeEvent.fire(CEntityFolder.this, getValue());
                            sheduled = false;
                        }
                    });
                }
            }
        });

        return item;
    }

    @Override
    public void initContent() {
        super.initContent();
        addValueChangeHandler((IFolderDecorator) getDecorator());
        //TODO use components inheritance
        if (this.getDebugId() != null) {
            getDecorator().asWidget().ensureDebugId(this.getDebugId().debugId() + IFolderDecorator.DEBUGID_SUFIX);
        }

        ((IFolderDecorator) getDecorator()).addItemAddClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addItem();
            }
        });
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        super.setDebugId(debugId);
        if ((debugId != null) && (folderDecorator != null)) {
            folderDecorator.asWidget().ensureDebugId(this.getDebugId().debugId() + IFolderDecorator.DEBUGID_SUFIX);
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

        final CEntityFolderItemEditor<E> comp = createItemPrivate(container.getWidgetCount() == 0);
        createNewEntity(newEntity, new DefaultAsyncCallback<E>() {
            @Override
            public void onSuccess(E result) {
                getValue().add(result);
                comp.populate(result);
                adoptFolderItem(comp);
                ValueChangeEvent.fire(CEntityFolder.this, getValue());
            }

        });

    }

    protected void removeItem(CEntityFolderItemEditor<E> item) {
        getValue().remove(item.getValue());
        abandonFolderItem(item);
        ValueChangeEvent.fire(CEntityFolder.this, getValue());
    }

    protected void moveUpItem(CEntityFolderItemEditor<E> item) {
        moveItem(item, true);

    }

    protected void moveDownItem(CEntityFolderItemEditor<E> item) {
        moveItem(item, false);
    }

    protected void moveItem(CEntityFolderItemEditor<E> item, boolean up) {
        int indexBefore = getValue().indexOf(item.getValue());
        int indexAfter = indexBefore + (up ? -1 : +1);
        if (indexAfter < 0 || indexAfter > getValue().size()) {
            return;
        }
        getValue().remove(indexBefore);
        getValue().add(indexAfter, item.getValue());

        container.clear();
        for (E entity : getValue()) {
            container.add(itemsMap.get(entity));
        }
        setNativeValue(getValue());
        ValueChangeEvent.fire(CEntityFolder.this, getValue());
        return;

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
            if (isFolderItemAllowed(item)) {
                CEntityFolderItemEditor<E> comp = null;
                if (oldMap.containsKey(item)) {
                    comp = oldMap.remove(item);
                    comp.setFirst(first);
                } else {
                    comp = createItemPrivate(first);
                }

                comp.populate(item);
                adoptFolderItem(comp);
                first = false;
            }
        }

        for (CEntityFolderItemEditor<E> item : oldMap.values()) {
            container.remove(item);
        }

        if (folderDecorator instanceof TableFolderDecorator) {
            ((TableFolderDecorator<E>) folderDecorator).setHeaderVisible(container.getWidgetCount() > 0);
        }
    }

    /**
     * Controls folder population with entity items.
     * 
     * @param item
     *            - processed entity item.
     * @return true if item is allowed, false - otherwise.
     */
    protected boolean isFolderItemAllowed(E item) {
        return true; // by default - all items are allowed!..
    }

    @Override
    protected abstract IFolderDecorator<E> createDecorator();

    private void adoptFolderItem(final CEntityFolderItemEditor<E> item) {

        item.addAccessAdapter(this);
        if (container.getWidgetIndex(item) == -1) {
            container.add(item);
        }
        itemsMap.put(item.getValue(), item);

        IDebugId rowDebugId = new CompositeDebugId(this.getDebugId(), "row", currentRowDebugId);
        item.setDebugId(rowDebugId);
        currentRowDebugId++;

        item.addItemRemoveClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                removeItem(item);
            }
        });
        item.addRowUpClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                moveUpItem(item);
            }
        });
        item.addRowDownClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                moveDownItem(item);
            }
        });

        item.onAdopt();
        ValueChangeEvent.fire(this, getValue());
    }

    private void abandonFolderItem(final CEntityFolderItemEditor<E> item) {
        container.remove(item);
        itemsMap.remove(item.getValue());
        item.onAbandon();
        ValueChangeEvent.fire(this, getValue());
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
