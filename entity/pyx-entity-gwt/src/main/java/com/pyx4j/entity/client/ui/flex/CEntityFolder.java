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
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.pyx4j.entity.client.ui.flex;

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
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.ValidationResults;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

/**
 * This component represents list of IEntities
 */
public abstract class CEntityFolder<E extends IEntity> extends CEditableComponent<IList<E>, NativeEntityFolder<IList<E>>> implements IComponentContainer,
        IFlexContentComponent {

    private static final Logger log = LoggerFactory.getLogger(CEntityFolder.class);

    private IFlexContentComponent bindParent;

    private FolderDecorator<E> folderDecorator;

    private final FlowPanel content;

    protected int currentRowDebugId = 0;

    private final LinkedHashMap<E, CEntityFolderItem<E>> itemsMap;

    private final EditableComponentsContainerHelper containerHelper;

    private final E entityPrototype;

    public CEntityFolder(Class<E> rowClass) {
        content = new FlowPanel();
        itemsMap = new LinkedHashMap<E, CEntityFolderItem<E>>();
        containerHelper = new EditableComponentsContainerHelper(this);
        if (rowClass != null) {
            entityPrototype = EntityFactory.getEntityPrototype(rowClass);
        } else {
            entityPrototype = null;
        }
    }

    /**
     * This mainly use for columns creation when TableFolderDecorator is used
     * 
     * @return
     */
    public E proto() {
        return entityPrototype;
    }

    @Override
    public void onBound(IFlexContentComponent parent) {
        bindParent = parent;
        setFolderDecorator(createContent());
        addValidations();
    }

    @Override
    public void addValidations() {

    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        return bindParent.create(member);
    }

    @Override
    public FolderDecorator<E> createContent() {
        return createFolderDecorator();
    }

    protected abstract CEntityFolderItem<E> createItem();

    private CEntityFolderItem<E> createItemPrivate() {

        CEntityFolderItem<E> item = createItem();

        item.addValueChangeHandler(new ValueChangeHandler<E>() {
            boolean sheduled = false;

            @Override
            public void onValueChange(final ValueChangeEvent<E> event) {
                if (!sheduled) {
                    sheduled = true;
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
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
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
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

    protected abstract FolderDecorator<E> createFolderDecorator();

    public void setFolderDecorator(FolderDecorator<E> folderDecorator) {
        this.folderDecorator = folderDecorator;

        addValueChangeHandler(folderDecorator);

        asWidget().setWidget(folderDecorator);

        folderDecorator.setFolder(this);

        folderDecorator.addItemAddClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addItem();
            }
        });
        //TODO use components inheritance
        if (this.getDebugId() != null) {
            folderDecorator.asWidget().ensureDebugId(this.getDebugId().getDebugIdString() + "_fd_");
        }
    }

    public FolderDecorator<E> getFolderDecorator() {
        return folderDecorator;
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        super.setDebugId(debugId);
        if ((debugId != null) && (folderDecorator != null)) {
            folderDecorator.asWidget().ensureDebugId(this.getDebugId().getDebugIdString() + "_fd_");
        }
    }

    protected void addItem() {
        if (getValue() == null) {
            log.warn("Request to add item has been issued before the form populated with value");
            return;
        }

        final CEntityFolderItem<E> comp = createItemPrivate();

        @SuppressWarnings("unchecked")
        E newEntity = (E) EntityFactory.create(comp.proto().getValueClass());

        createNewEntity(newEntity, new DefaultAsyncCallback<E>() {

            @Override
            public void onSuccess(E result) {
                comp.setFirst(content.getWidgetCount() == 0);
                getValue().add(result);
                comp.onBound(CEntityFolder.this);
                comp.populate(result);
                adoptFolderItem(comp);
                ValueChangeEvent.fire(CEntityFolder.this, getValue());
            }

        });

    }

    protected void removeItem(CEntityFolderItem<E> item, FolderItemDecorator folderItemDecorator) {
        getValue().remove(item.getValue());
        abandonFolderItem(item);
        item.removeAllHandlers();
        ValueChangeEvent.fire(CEntityFolder.this, getValue());
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
        HashMap<E, CEntityFolderItem<E>> oldMap = new HashMap<E, CEntityFolderItem<E>>(itemsMap);

        itemsMap.clear();
        currentRowDebugId = 0;

        boolean first = true;
        for (E item : value) {
            CEntityFolderItem<E> comp = null;
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

        for (CEntityFolderItem<E> item : oldMap.values()) {
            content.remove(item);
        }

        if (folderDecorator instanceof TableFolderDecorator) {
            ((TableFolderDecorator<E>) folderDecorator).setHeaderVisible(content.getWidgetCount() > 0);
        }

    }

    private void abandonFolderItem(final CEntityFolderItem<E> component) {
        content.remove(component);
        itemsMap.remove(component.getValue());
        ValueChangeEvent.fire(this, getValue());
    }

    private void adoptFolderItem(final CEntityFolderItem<E> component) {

        final FolderItemDecorator folderItemDecorator = component.createFolderItemDecorator();

        component.setFolderItemDecorator(folderItemDecorator);
        component.addAccessAdapter(containerHelper);
        if (content.getWidgetIndex(component) == -1) {
            content.add(component);
        }
        itemsMap.put(component.getValue(), component);

        currentRowDebugId++;
        IDebugId rowDebugId = new CompositeDebugId(this.getDebugId(), "row", currentRowDebugId);
        component.setDebugId(rowDebugId);
        folderItemDecorator.asWidget().ensureDebugId(rowDebugId.getDebugIdString());

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

    protected CEntityFolderItem<E> getFolderRow(E value) {
        return itemsMap.get(value);
    }

    @Override
    protected NativeEntityFolder<IList<E>> createWidget() {
        return new NativeEntityFolder<IList<E>>();
    }

    @Override
    public boolean isValid() {
        if (!isEditable() || !isEnabled()) {
            return true;
        }
        if (!super.isValid()) {
            return false;
        } else {
            return containerHelper.isValid();
        }
    }

    @Override
    public ValidationResults getValidationResults() {
        return containerHelper.getAllValidationResults();
    }

    public ValidationResults getContainerValidationResults() {
        return containerHelper.getContainerValidationResults();
    }

    @Override
    public void setVisited(boolean visited) {
        super.setVisited(visited);
        containerHelper.setVisited(visited);
    }

    public FlowPanel getContent() {
        return content;
    }

    @Override
    public boolean isVisited() {
        return true;
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
