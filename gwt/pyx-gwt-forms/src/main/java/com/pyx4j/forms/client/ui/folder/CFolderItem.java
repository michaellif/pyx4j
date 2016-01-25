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
 */
package com.pyx4j.forms.client.ui.folder;

import java.util.Arrays;
import java.util.Collection;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CContainer;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.ItemActionsBar.ActionType;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.images.ButtonImages;

public abstract class CFolderItem<DATA_TYPE extends IEntity> extends CContainer<CFolderItem<DATA_TYPE>, DATA_TYPE, IFolderItemDecorator<DATA_TYPE>> {

    private static final I18n i18n = I18n.get(CFolderItem.class);

    private boolean first;

    private boolean last;

    private boolean movable = true;

    private boolean removable = true;

    private HandlerRegistration folderHandlerRegistration;

    private CForm<DATA_TYPE> entityForm;

    private final Class<DATA_TYPE> clazz;

    private ItemActionsBar actionsBar;

    public CFolderItem(Class<DATA_TYPE> clazz) {
        this(clazz, true, true);
    }

    public CFolderItem(Class<DATA_TYPE> clazz, boolean movable, boolean removable) {
        super();
        this.clazz = clazz;
        this.movable = movable;
        this.removable = removable;

        actionsBar = new ItemActionsBar(removable);

        IFolderItemDecorator<DATA_TYPE> decorator = createItemDecorator();

        FolderImages images = decorator.getImages();

        addAction(ActionType.Remove, i18n.tr("Delete Item"), images.delButton(), null);
        addAction(ActionType.Up, i18n.tr("Move up"), images.moveUpButton(), null);
        addAction(ActionType.Down, i18n.tr("Move down"), images.moveDownButton(), null);

        setDecorator(decorator);

        initActionBar();

    }

    protected abstract IFolderItemDecorator<DATA_TYPE> createItemDecorator();

    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public boolean isMovable() {
        return movable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

    public boolean isRemovable() {
        return removable;
    }

    @Override
    protected IsWidget createContent() {
        entityForm = (CForm<DATA_TYPE>) createItemForm(EntityFactory.getEntityPrototype(clazz));
        adopt(entityForm);
        return entityForm;
    }

    public CForm<DATA_TYPE> getEntityForm() {
        return entityForm;
    }

    protected abstract CForm<? extends DATA_TYPE> createItemForm(IObject<?> member);

    public void addAction(ActionType action, String title, ButtonImages images, Command command) {
        actionsBar.addAction(action, title, images, command);
        initActionBar();
    }

    private void initActionBar() {
        actionsBar.init(getDecorator() instanceof BoxFolderItemDecorator ? Direction.RTL : Direction.LTR);
    }

    private void setActionCommand(ActionType type, Command command) {
        actionsBar.setActionCommand(type, command);
    }

    public ItemActionsBar getItemActionsBar() {
        return actionsBar;
    }

    @Override
    public void onAbandon() {
        super.onAbandon();
        if (folderHandlerRegistration != null) {
            folderHandlerRegistration.removeHandler();
            folderHandlerRegistration = null;
        }
    }

    @Override
    public void onAdopt(final CContainer<?, ?, ?> parent) {
        super.onAdopt(parent);

        final CFolder<DATA_TYPE> folder = (CFolder<DATA_TYPE>) parent;

        folderHandlerRegistration = folder.addValueChangeHandler(new ValueChangeHandler<IList<DATA_TYPE>>() {

            @Override
            public void onValueChange(ValueChangeEvent<IList<DATA_TYPE>> event) {
                calculateActionsState();
            }
        });

        setActionCommand(ActionType.Remove, new Command() {
            @Override
            public void execute() {
                folder.removeItem(CFolderItem.this);
            }
        });
        setActionCommand(ActionType.Up, new Command() {
            @Override
            public void execute() {
                folder.moveUpItem(CFolderItem.this);
            }
        });
        setActionCommand(ActionType.Down, new Command() {
            @Override
            public void execute() {
                folder.moveDownItem(CFolderItem.this);
            }
        });

        calculateActionsState();

    }

    @SuppressWarnings("unchecked")
    protected void calculateActionsState() {
        boolean enabled = getParent().isEnabled() && getParent().isEditable() && !getParent().isViewable();
        if (!enabled) {
            actionsBar.setDefaultActionsState(false, false, false);
        } else {

            CFolder<DATA_TYPE> parent = ((CFolder<DATA_TYPE>) getParent());
            int index = parent.getItemIndex(CFolderItem.this);

            first = index == 0;
            last = index == parent.getItemCount() - 1;

            CFolderItem<?> previousSibling = parent.getItem(index - 1);
            CFolderItem<?> nextSibling = parent.getItem(index + 1);

            actionsBar.setDefaultActionsState(removable && parent.isRemovable(), movable && !first && previousSibling.isMovable() && parent.isOrderable(),
                    movable && !last && nextSibling.isMovable() && parent.isOrderable());
        }

    }

    @Override
    protected void setComponentsValue(DATA_TYPE entity, boolean fireEvent, boolean populate) {
        entityForm.setValue(entity, fireEvent, populate);
    }

    @Override
    public Collection<CComponent<?>> getComponents() {
        if (entityForm == null) {
            return null;
        }
        return Arrays.asList(new CComponent<?>[] { entityForm });
    }

    public ValidationResults getComponentsValidationResults() {
        ValidationResults results = new ValidationResults();
        for (CComponent<?> component : entityForm.getComponents()) {
            if (!component.isValid()) {
                results.appendValidationResults(component.getValidationResults());
            }
        }
        return results;
    }
}
