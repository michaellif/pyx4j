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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.client.ui.flex.CEntity;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CContainer;
import com.pyx4j.forms.client.ui.ValidationResults;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

/**
 * This component represents list of IEntities
 */
public abstract class CEntityFolder<E extends IEntity> extends CEntity<IList<E>> {

    private static final Logger log = LoggerFactory.getLogger(CEntityFolder.class);

    public static enum StyleName implements IStyleName {
        EntityFolder, EntityFolderActionsBar, EntityFolderAddButton, EntityFolderAddButtonImage, EntityFolderAddButtonLabel,

        EntityFolderRemoveButton, EntityFolderUpButton, EntityFolderDownButton,

        //Box
        EntityFolderBoxItem, EntityFolderBoxDecorator, EntityFolderBoxItemDecorator,

        //Table
        EntityFolderRowItem, EntityFolderTableDecorator, EntityFolderRowItemDecorator, EntityFolderTableHeader, EntityFolderTableHeaderLabel
    }

    public static enum StyleDependent implements IStyleDependent {
        hover, readOnly
    }

    private final FlowPanel container;

    public boolean orderable = true;

    public boolean modifiable = true;

    protected int currentRowDebugId = 0;

    private final List<CEntityFolderItem<E>> itemsList;

    private final E entityPrototype;

    private final Class<E> rowClass;

    public CEntityFolder(Class<E> rowClass) {
        this.rowClass = rowClass;
        container = new FlowPanel();
        asWidget().setStyleName(StyleName.EntityFolder.name());
        itemsList = new ArrayList<CEntityFolderItem<E>>();
        if (rowClass != null) {
            entityPrototype = EntityFactory.getEntityPrototype(rowClass);
        } else {
            entityPrototype = null;
        }
    }

    public boolean isOrderable() {
        return orderable;
    }

    public void setOrderable(boolean orderable) {
        this.orderable = orderable;
        for (CEntityFolderItem<E> item : itemsList) {
            item.setMovable(orderable);
            item.calculateActionsState();
        }
    }

    public boolean isModifiable() {
        return modifiable;
    }

    public void setModifiable(boolean modifiable) {
        this.modifiable = modifiable;
        if (getDecorator() != null) {
            ((IFolderDecorator) getDecorator()).setAddButtonVisible(modifiable);
        }
        for (CEntityFolderItem<E> item : itemsList) {
            item.setRemovable(modifiable);
            item.calculateActionsState();
        }
    }

    /**
     * This mainly use for columns creation when TableFolderDecorator is used
     */
    public E proto() {
        return entityPrototype;
    }

    @Override
    public void onAttach(CContainer<?, ?> parent) {
        super.onAttach(parent);
        initContent();
        addValidations();
    }

    @Override
    public IsWidget createContent() {
        // TODO Auto-generated method stub
        return null;
    }

    protected abstract IFolderItemDecorator<E> createItemDecorator();

    protected CEntityFolderItem<E> createItem(boolean first) {
        return new CEntityFolderItem<E>(rowClass) {
            @Override
            public IFolderItemDecorator<E> createDecorator() {
                return createItemDecorator();
            }

        };
    }

    private CEntityFolderItem<E> createItemPrivate() {

        boolean first = container.getWidgetCount() == 0;

        CEntityFolderItem<E> item = createItem(first);

        item.onAttach(this);

        if (modifiable == false) {
            item.setRemovable(false);
        }

        if (orderable == false) {
            item.setMovable(false);
        }

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
                            PropertyChangeEvent.fire(CEntityFolder.this, PropertyChangeEvent.PropertyName.enabled);
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

        ((IFolderDecorator) getDecorator()).setAddButtonVisible(modifiable);

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
        if ((debugId != null) && (getDecorator() != null)) {
            getDecorator().asWidget().ensureDebugId(this.getDebugId().debugId() + IFolderDecorator.DEBUGID_SUFIX);
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

        final CEntityFolderItem<E> item = createItemPrivate();
        createNewEntity(newEntity, new DefaultAsyncCallback<E>() {
            @Override
            public void onSuccess(E result) {
                getValue().add(result);
                item.populate(result);
                adoptItem(item);
                ValueChangeEvent.fire(CEntityFolder.this, getValue());
            }
        });

    }

    protected void removeItem(CEntityFolderItem<E> item) {
        getValue().remove(item.getValue());
        abandonItem(item);
        ValueChangeEvent.fire(CEntityFolder.this, getValue());
    }

    protected void moveUpItem(CEntityFolderItem<E> item) {
        moveItem(item, true);

    }

    protected void moveDownItem(CEntityFolderItem<E> item) {
        moveItem(item, false);
    }

    protected void moveItem(CEntityFolderItem<E> item, boolean up) {
        int indexBefore = getValue().indexOf(item.getValue());
        int indexAfter = indexBefore + (up ? -1 : +1);
        if (indexAfter < 0 || indexAfter > getValue().size()) {
            return;
        }

        getValue().remove(indexBefore);
        itemsList.remove(indexBefore);
        container.remove(item);

        getValue().add(indexAfter, item.getValue());
        itemsList.add(indexAfter, item);
        container.insert(item, indexAfter);

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
        ArrayList<CEntityFolderItem<E>> previousList = new ArrayList<CEntityFolderItem<E>>(itemsList);

        for (CEntityFolderItem<E> item : previousList) {
            abandonItem(item);
        }

        currentRowDebugId = 0;

        for (E entity : value) {
            if (isFolderItemAllowed(entity)) {
                CEntityFolderItem<E> item = null;
                for (CEntityFolderItem<E> itemFromCahe : previousList) {
                    if (itemFromCahe.getValue().equals(entity)) {
                        previousList.remove(itemFromCahe);
                        item = itemFromCahe;
                        break;
                    }
                }
                if (item == null) {
                    item = createItemPrivate();
                }
                adoptItem(item);
                item.populate(entity);
            }
        }

        for (CEntityFolderItem<E> item : itemsList) {
            item.calculateActionsState();
        }

        if (getDecorator() instanceof TableFolderDecorator) {
            ((TableFolderDecorator<E>) getDecorator()).setHeaderVisible(container.getWidgetCount() > 0);
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

    private void adoptItem(final CEntityFolderItem<E> item) {
        itemsList.add(item);
        container.add(item);

        item.addAccessAdapter(this);

        IDebugId rowDebugId = new CompositeDebugId(this.getDebugId(), "row", currentRowDebugId);
        item.setDebugId(rowDebugId);
        currentRowDebugId++;

        item.onAdopt(this);

    }

    private void abandonItem(final CEntityFolderItem<E> item) {
        container.remove(item);
        itemsList.remove(item);
        item.removeAccessAdapter(this);

        item.onAbandon();
    }

    @Override
    public Collection<? extends CComponent<?, ?>> getComponents() {
        if (itemsList != null) {
            return itemsList;
        } else {
            return null;
        }
    }

    @Override
    public ValidationResults getValidationResults() {
        return getAllValidationResults();
    }

    @Override
    public FlowPanel getContainer() {
        return container;
    }

    @Override
    public void applyVisibilityRules() {
        super.applyVisibilityRules();
        if (getComponents() != null) {
            for (CComponent<?, ?> component : getComponents()) {
                component.applyVisibilityRules();
            }
        }
    }

    @Override
    public void applyEnablingRules() {
        super.applyEnablingRules();
        if (getComponents() != null) {
            for (CComponent<?, ?> component : getComponents()) {
                component.applyEnablingRules();
            }
        }
    }

    @Override
    public void applyEditabilityRules() {
        if (getComponents() != null) {
            for (CComponent<?, ?> component : getComponents()) {
                if (component instanceof CComponent<?, ?>) {
                    ((CComponent<?, ?>) component).applyEditabilityRules();
                }
            }
        }
    }

    public int getItemCount() {
        return itemsList.size();
    }

    public int getItemIndex(CEntityFolderItem item) {
        return itemsList.indexOf(item);
    }

    public CEntityFolderItem getItem(int index) {
        if (itemsList.size() > 0 && index > -1 && index < itemsList.size()) {
            return itemsList.get(index);
        } else {
            return null;
        }
    }

    @Override
    public void addComponent(CComponent<?, ?> component) {
        // TODO Auto-generated method stub

    }
}
