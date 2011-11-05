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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.client.ui.flex.CEntity;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.ValidationResults;

/**
 * This component represents list of IEntities
 */
public abstract class CPolymorphicEntityEditor<E extends IEntity> extends CEntityEditor<E> {

    private static final Logger log = LoggerFactory.getLogger(CPolymorphicEntityEditor.class);

    private IPolymorphicEditorDecorator<E> decorator;

    private final SimplePanel container;

    private final List<? extends IDiscriminator<E>> discriminators;

    private final HashMap<? extends IDiscriminator<? extends E>, IEntity> subtypesHash;

    protected int currentRowDebugId = 0;

    public CPolymorphicEntityEditor(Class<E> rowClass, List<? extends IDiscriminator<E>> discriminators) {
        super(rowClass);
        this.discriminators = discriminators;
        container = new SimplePanel();
        container.getElement().setClassName("TESTTEST");
        subtypesHash = new HashMap<IDiscriminator<? extends E>, IEntity>();
    }

    @Override
    public void onBound(CEntity<?> parent) {
        super.onBound(parent);
        setDecorator(createContent());
        addValidations();
    }

    @Override
    public IPolymorphicEditorDecorator<E> createContent() {
        return createDecorator();
    }

    protected abstract CEntityEditor<? extends E> createItem(IDiscriminator<E> discriminator);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private CEntityEditor<? extends E> createItemPrivate(IDiscriminator<E> discriminator) {

        CEntityEditor<? extends E> item = createItem(discriminator);

        if (item == null) {
            throw new Error("Failed to create item for discriminator " + discriminator);
        }

        item.addValueChangeHandler(new ValueChangeHandler() {
            boolean sheduled = false;

            @Override
            public void onValueChange(final ValueChangeEvent event) {
                if (!sheduled) {
                    sheduled = true;
                    Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            log.debug("CEntityFolder.onValueChange fired from {}. New value is {}.", CPolymorphicEntityEditor.this.getTitle(), event.getValue());
                            revalidate();
                            ValueChangeEvent.fire(CPolymorphicEntityEditor.this, getValue());
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
                            log.debug("CEntityFolder.onPropertyChange fired from {}. Changed property is {}.", CPolymorphicEntityEditor.this.getTitle(),
                                    event.getPropertyName());
                            revalidate();
                            ValueChangeEvent.fire(CPolymorphicEntityEditor.this, getValue());
                            sheduled = false;
                        }
                    });
                }
            }
        });

        return item;
    }

    @Override
    protected abstract IPolymorphicEditorDecorator<E> createDecorator();

    public void setDecorator(IPolymorphicEditorDecorator<E> decorator) {
        this.decorator = decorator;

        addValueChangeHandler(decorator);

        asWidget().setWidget(decorator);

        decorator.setEditor(this);

        //TODO use components inheritance
        if (this.getDebugId() != null) {
            decorator.asWidget().ensureDebugId(this.getDebugId().debugId() + IFolderDecorator.DEBUGID_SUFIX);
        }
    }

    public IPolymorphicEditorDecorator<E> getPolymorphicEditorDecorator() {
        return decorator;
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        super.setDebugId(debugId);
        if ((debugId != null) && (decorator != null)) {
            decorator.asWidget().ensureDebugId(this.getDebugId().debugId() + IFolderDecorator.DEBUGID_SUFIX);
        }
    }

//    protected void setItem(Class<E> type) {
//        addItem(EntityFactory.create(type));
//    }
//
//    protected void addItem(E newEntity) {
//        if (getValue() == null) {
//            log.warn("Request to add item has been issued before the form populated with value");
//            return;
//        }
//
//        final CEntityFolderItemEditor<E> comp = createItemPrivate();
//        createNewEntity(newEntity, new DefaultAsyncCallback<E>() {
//            @Override
//            public void onSuccess(E result) {
//                comp.setFirst(container.getWidgetCount() == 0);
//                getValue().add(result);
//                comp.onBound(CPolymorphicEntityEditor.this);
//                comp.populate(result);
//                adoptFolderItem(comp);
//                ValueChangeEvent.fire(CPolymorphicEntityEditor.this, getValue());
//            }
//
//        });
//
//    }
//
//    protected void removeItem(CEntityFolderItemEditor<E> item, IFolderItemEditorDecorator<E> folderItemDecorator) {
//        getValue().remove(item.getValue());
//        abandonFolderItem(item);
//        item.removeAllHandlers();
//        ValueChangeEvent.fire(CPolymorphicEntityEditor.this, getValue());
//    }
//
//    /**
//     * Implementation to override new Entity creation. No need to call
//     * super.createNewEntity().
//     * 
//     * @param newEntity
//     * @param callback
//     */
//    protected void createNewEntity(E newEntity, AsyncCallback<E> callback) {
//        callback.onSuccess(newEntity);
//    }

    @Override
    public void setValue(E value) {
        super.setValue(value);
        repopulate(value);
    }

    @Override
    public void populate(E value) {
        super.populate(value);
        repopulate(value);
    }

    protected void repopulate(E value) {
        for (IDiscriminator<E> discriminator : discriminators) {
            if (discriminator.getType().equals(value.getObjectClass())) {
                CEntityEditor editor = createItemPrivate(discriminator);
                editor.onBound(this);
                editor.populate(value);
                container.setWidget(editor);

                break;
            }
        }

    }

//    protected void repopulate(IList<E> value) {
//        HashMap<E, CEntityEditor<E>> oldMap = new HashMap<E, CEntityEditor<E>>(itemsMap);
//
//        itemsMap.clear();
//        currentRowDebugId = 0;
//
//        boolean first = true;
//        for (E item : value) {
//            if (isFolderItemAllowed(item)) {
//                CEntityFolderItemEditor<E> comp = null;
//                if (oldMap.containsKey(item)) {
//                    comp = oldMap.remove(item);
//                    comp.setFirst(first);
//                } else {
//                    comp = createItemPrivate();
//                    //Call setFirst before onBound()
//                    comp.setFirst(first);
//                    comp.onBound(this);
//                }
//
//                comp.populate(item);
//                adoptFolderItem(comp);
//                first = false;
//            }
//        }
//
//        for (CEntityFolderItemEditor<E> item : oldMap.values()) {
//            container.remove(item);
//        }
//
//    }

//    private void abandonFolderItem(final CEntityFolderItemEditor<E> component) {
//        container.remove(component);
//        itemsMap.remove(component.getValue());
//        ValueChangeEvent.fire(this, getValue());
//    }
//
//    private void adoptFolderItem(final CEntityFolderItemEditor<E> component) {
//
//        final IFolderItemEditorDecorator<E> folderItemDecorator = component.createFolderItemDecorator();
//
//        component.setFolderItemDecorator(folderItemDecorator);
//        component.addAccessAdapter(this);
//        if (container.getWidgetIndex(component) == -1) {
//            container.add(component);
//        }
//        itemsMap.put(component.getValue(), component);
//
//        IDebugId rowDebugId = new CompositeDebugId(this.getDebugId(), "row", currentRowDebugId);
//        currentRowDebugId++;
//
//        component.setDebugId(rowDebugId);
//        folderItemDecorator.asWidget().ensureDebugId(rowDebugId.debugId());
//
//        folderItemDecorator.addItemRemoveClickHandler(new ClickHandler() {
//
//            @Override
//            public void onClick(ClickEvent event) {
//                removeItem(component, folderItemDecorator);
//            }
//        });
//
//    }

    @Override
    public Collection<? extends CEditableComponent<?, ?>> getComponents() {
//        if (itemsMap != null) {
//            return itemsMap.values();
//        } else {
        return null;
//        }
    }

    @Override
    public ValidationResults getValidationResults() {
        return getAllValidationResults();
    }

    @Override
    public Panel getContainer() {
        return container;
    }

    @Override
    public void applyVisibilityRules() {
        super.applyVisibilityRules();
        if (getComponents() != null) {
            for (CEditableComponent<?, ?> component : getComponents()) {
                component.applyVisibilityRules();
            }
        }
    }

    @Override
    public void applyEnablingRules() {
        super.applyEnablingRules();
        if (getComponents() != null) {
            for (CEditableComponent<?, ?> component : getComponents()) {
                component.applyEnablingRules();
            }
        }
    }

    @Override
    public void applyEditabilityRules() {
        if (getComponents() != null) {
            for (CEditableComponent<?, ?> component : getComponents()) {
                if (component instanceof CEditableComponent<?, ?>) {
                    ((CEditableComponent<?, ?>) component).applyEditabilityRules();
                }
            }
        }
    }
}
