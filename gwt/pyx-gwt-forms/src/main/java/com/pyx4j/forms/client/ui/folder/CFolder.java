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
 */
package com.pyx4j.forms.client.ui.folder;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.pyx4j.gwt.commons.ui.Label;
import com.pyx4j.gwt.commons.ui.SimplePanel;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.annotations.SecurityEnabled;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CContainer;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.shared.SecurityController;

/**
 * This component represents list of IEntities
 */
public abstract class CFolder<DATA_TYPE extends IEntity> extends CContainer<CFolder<DATA_TYPE>, IList<DATA_TYPE>, IFolderDecorator<DATA_TYPE>> {

    private static final Logger log = LoggerFactory.getLogger(CFolder.class);

    private static final I18n i18n = I18n.get(CFolder.class);

    private final FlowPanel container;

    private final SimplePanel noDataNotificationHolder;

    private boolean orderable = true;

    private boolean addable = true;

    private boolean removable = true;

    private int currentRowDebugId = 0;

    private final List<CFolderItem<DATA_TYPE>> itemsList;

    private final DATA_TYPE entityPrototype;

    private final Class<DATA_TYPE> entityClass;

    public CFolder(Class<DATA_TYPE> entityClass) {
        assert (entityClass != null);
        this.entityClass = entityClass;
        asWidget().setStyleName(FolderTheme.StyleName.CFolder.name());
        itemsList = new ArrayList<CFolderItem<DATA_TYPE>>();

        container = new FlowPanel();

        noDataNotificationHolder = new SimplePanel();
        noDataNotificationHolder.setStyleName(FolderTheme.StyleName.CFolderNoDataMessage.name());
        noDataNotificationHolder.setWidget(new Label(i18n.tr("No Data")));

        entityPrototype = EntityFactory.getEntityPrototype(entityClass);

        addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.enabled, PropertyName.editable, PropertyName.repopulated)) {
                    calculateActionsState();
                }
            }
        });

        addValueChangeHandler(new ValueChangeHandler<IList<DATA_TYPE>>() {

            @Override
            public void onValueChange(ValueChangeEvent<IList<DATA_TYPE>> event) {
                calculateActionsState();
            }
        });
    }

    @Override
    protected IList<DATA_TYPE> preprocessValue(IList<DATA_TYPE> value, boolean fireEvent, boolean populate) {
        if (!populate && value != null) {
            IList<DATA_TYPE> boundList = getValue();
            // Avoid self cleaning
            if ((boundList != value) && (boundList.getValue() != value.getValue())) {
                boundList.clear();
                boundList.addAll(value);
            }
            value = boundList;
        }
        return super.preprocessValue(value, fireEvent, populate);
    }

    public boolean isOrderable() {
        return orderable;
    }

    public void setOrderable(boolean orderable) {
        this.orderable = orderable;
        calculateActionsState();
    }

    public final boolean isAddable() {
        if (EntityFactory.getEntityMeta(entityClass).isAnnotationPresent(SecurityEnabled.class)) {
            return addable && SecurityController.check(DataModelPermission.permissionCreate(entityClass));
        } else {
            return addable;
        }
    }

    public void setAddable(boolean addable) {
        this.addable = addable;
        calculateActionsState();
    }

    public final boolean isRemovable() {
        if (EntityFactory.getEntityMeta(entityClass).isAnnotationPresent(SecurityEnabled.class)) {
            return removable && SecurityController.check(DataModelPermission.permissionDelete(entityClass));
        } else {
            return removable;
        }
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
        calculateActionsState();
    }

    public void setModifyable(boolean modifyable) {
        setAddable(modifyable);
        setRemovable(modifyable);
        setOrderable(modifyable);
    }

    public void setNoDataLabel(String text) {
        noDataNotificationHolder.setWidget(new Label(text));
    }

    private void calculateActionsState() {
        boolean addable = isAddable() && isEnabled() && isEditable() && !isViewable();
        if (getDecorator() instanceof IFolderDecorator) {
            ((IFolderDecorator<?>) getDecorator()).setAddButtonVisible(addable);
        }
        for (CFolderItem<DATA_TYPE> item : itemsList) {
            item.calculateActionsState();
        }
    }

    /**
     * This mainly use for columns creation when TableFolderDecorator is used
     */
    public final DATA_TYPE proto() {
        return entityPrototype;
    }

    public final Class<DATA_TYPE> getEntityClass() {
        return entityClass;
    }

    @Override
    protected IsWidget createContent() {
        FlowPanel contentPanel = new FlowPanel();
        contentPanel.add(noDataNotificationHolder);
        contentPanel.add(container);
        return contentPanel;
    }

    protected abstract IFolderItemDecorator<DATA_TYPE> createItemDecorator();

    protected abstract CForm<? extends DATA_TYPE> createItemForm(IObject<?> member);

    protected CFolderItem<DATA_TYPE> createItem(boolean first) {
        return new CFolderItem<DATA_TYPE>(entityClass) {
            @Override
            public IFolderItemDecorator<DATA_TYPE> createItemDecorator() {
                return CFolder.this.createItemDecorator();
            }

            @Override
            protected CForm<? extends DATA_TYPE> createItemForm(IObject<?> member) {
                return CFolder.this.createItemForm(null);
            }

        };
    }

    private CFolderItem<DATA_TYPE> createItemPrivate() {
        return createItem(container.getWidgetCount() == 0);
    }

    protected abstract IFolderDecorator<DATA_TYPE> createFolderDecorator();

    @Override
    protected final IFolderDecorator<DATA_TYPE> createDecorator() {
        IFolderDecorator<DATA_TYPE> folderDecorator = createFolderDecorator();
        folderDecorator.setAddButtonVisible(addable);

        addValueChangeHandler(folderDecorator);

        folderDecorator.setItemAddCommand(new Command() {

            @Override
            public void execute() {
                addItem();
            }
        });

        return folderDecorator;
    }

    protected void addItem() {
        createNewEntity(new DefaultAsyncCallback<DATA_TYPE>() {
            @Override
            public void onSuccess(DATA_TYPE result) {
                addItem(result);
            }
        });
    }

    protected void addItem(DATA_TYPE newEntity) {
        if (getValue() == null) {
            log.warn("Request to add item has been issued before the form populated with value");
            return;
        }

        final CFolderItem<DATA_TYPE> item = createItemPrivate();

        adopt(item);
        getValue().add(newEntity);
        item.populate(newEntity);
        setVisited(true);

        revalidate();
        ValueChangeEvent.fire(CFolder.this, getValue());

        if (item.getDecorator() instanceof BoxFolderItemDecorator) {
            ((BoxFolderItemDecorator<?>) item.getDecorator()).setExpended(true);
        }

    }

    protected void removeItem(CFolderItem<DATA_TYPE> item) {
        abandon(item);
        getValue().remove(item.getValue());
        setVisited(true);
        revalidate();
        ValueChangeEvent.fire(CFolder.this, getValue());

    }

    protected void moveUpItem(CFolderItem<DATA_TYPE> item) {
        moveItem(item, true);
    }

    protected void moveDownItem(CFolderItem<DATA_TYPE> item) {
        moveItem(item, false);
    }

    protected void moveItem(CFolderItem<DATA_TYPE> item, boolean up) {
        int indexBefore = getValue().indexOf(item.getValue());
        int indexAfter = indexBefore + (up ? -1 : +1);
        if (indexAfter < 0 || indexAfter > getValue().size()) {
            return;
        }

        getValue().move(indexBefore, indexAfter);
        itemsList.remove(indexBefore);
        container.remove(item);

        itemsList.add(indexAfter, item);
        container.insert(item, indexAfter);

        setVisited(true);
        revalidate();
        ValueChangeEvent.fire(CFolder.this, getValue());
        return;
    }

    /**
     * Implementation to override new Entity creation. No need to call
     * super.createNewEntity().
     *
     * @param newEntity
     * @param callback
     */
    @SuppressWarnings("unchecked")
    protected void createNewEntity(AsyncCallback<DATA_TYPE> callback) {
        callback.onSuccess((DATA_TYPE) EntityFactory.create(entityPrototype.getValueClass()));
    }

    @Override
    protected void setComponentsValue(IList<DATA_TYPE> value, boolean fireEvent, boolean populate) {

        ArrayList<CFolderItem<DATA_TYPE>> previousList = new ArrayList<CFolderItem<DATA_TYPE>>(itemsList);

        for (CFolderItem<DATA_TYPE> item : previousList) {
            abandon(item);
        }

        currentRowDebugId = 0;

        if (value != null) {

            for (DATA_TYPE entity : value) {
                CFolderItem<DATA_TYPE> item = null;
                for (CFolderItem<DATA_TYPE> itemFromCahe : previousList) {
                    if (itemFromCahe.getValue().equals(entity)) {
                        previousList.remove(itemFromCahe);
                        item = itemFromCahe;
                        break;
                    }
                }

                if (item == null) {
                    item = createItemPrivate();
                    adopt(item);
                    item.populate(entity);
                } else {
                    adopt(item);
                }

                item.setValue(entity, fireEvent, populate);
            }

        }

        for (CFolderItem<DATA_TYPE> item : itemsList) {
            item.calculateActionsState();
        }

        if (getDecorator() instanceof TableFolderDecorator) {
            ((TableFolderDecorator<?>) getDecorator()).setHeaderVisible(container.getWidgetCount() > 0);
        }
    }

    @Override
    public void adopt(final CComponent<?> component) {
        itemsList.add((CFolderItem<DATA_TYPE>) component);
        container.add(component);

        IDebugId rowDebugId = new CompositeDebugId(IDebugId.ROW_PREFIX, currentRowDebugId);
        component.setDebugIdSuffix(rowDebugId);
        currentRowDebugId++;

        noDataNotificationHolder.setVisible(getItemCount() <= 0);

        super.adopt(component);

    }

    @Override
    public void abandon(final CComponent<?> component) {
        super.abandon(component);
        container.remove(component);
        itemsList.remove(component);

        noDataNotificationHolder.setVisible(getItemCount() <= 0);

    }

    @Override
    public List<CFolderItem<DATA_TYPE>> getComponents() {
        return itemsList;
    }

    public int getItemCount() {
        return itemsList.size();
    }

    public int getItemIndex(CFolderItem<DATA_TYPE> item) {
        return itemsList.indexOf(item);
    }

    public CFolderItem<DATA_TYPE> getItem(int index) {
        if (itemsList.size() > 0 && index > -1 && index < itemsList.size()) {
            return itemsList.get(index);
        } else {
            return null;
        }
    }

    @Override
    protected void onReset() {
        for (CComponent<?> component : new ArrayList<CComponent<?>>(getComponents())) {
            abandon(component);
        }
        container.clear();
        itemsList.clear();
    };

    public void setNoDataNotificationWidget(IsWidget widget) {
        noDataNotificationHolder.setWidget(widget);
    }
}
