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
import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityContainer;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.ui.folder.ItemActionsBar.ActionType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.images.ButtonImages;

public abstract class CEntityFolderItem<E extends IEntity> extends CEntityContainer<CEntityFolderItem<E>, E> {

    private static final I18n i18n = I18n.get(CEntityFolderItem.class);

    private boolean first;

    private boolean last;

    private boolean movable = true;

    private boolean removable = true;

    private HandlerRegistration folderHandlerRegistration;

    private CEntityForm<E> entityForm;

    private final Class<E> clazz;

    private ItemActionsBar actionsBar;

    public CEntityFolderItem(Class<E> clazz) {
        this(clazz, true, true);
    }

    public CEntityFolderItem(Class<E> clazz, boolean movable, boolean removable) {
        super();
        this.clazz = clazz;
        this.movable = movable;
        this.removable = removable;

        actionsBar = new ItemActionsBar(removable);

        IFolderItemDecorator<E> decorator = createItemDecorator();

        EntityFolderImages images = decorator.getImages();

        addAction(ActionType.Remove, i18n.tr("Delete Item"), images.delButton(), null);
        addAction(ActionType.Up, i18n.tr("Move up"), images.moveUpButton(), null);
        addAction(ActionType.Down, i18n.tr("Move down"), images.moveDownButton(), null);

        setDecorator(decorator);

        initActionBar();

    }

    protected abstract IFolderItemDecorator<E> createItemDecorator();

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
        entityForm = (CEntityForm) create(EntityFactory.getEntityPrototype(clazz));
        adopt(entityForm);
        return entityForm;
    }

    @Override
    public void setDecorator(IDecorator<? super CEntityFolderItem<E>> decorator) {
        super.setDecorator(decorator);
        ((IFolderItemDecorator) decorator).adoptItemActionsBar();
    }

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
    public void onAdopt(final CEntityContainer<?, ?> parent) {
        super.onAdopt(parent);

        final CEntityFolder<E> folder = (CEntityFolder<E>) parent;

        folderHandlerRegistration = folder.addValueChangeHandler(new ValueChangeHandler<IList<E>>() {

            @Override
            public void onValueChange(ValueChangeEvent<IList<E>> event) {
                calculateActionsState();
            }
        });

        setActionCommand(ActionType.Remove, new Command() {
            @Override
            public void execute() {
                folder.removeItem(CEntityFolderItem.this);
            }
        });
        setActionCommand(ActionType.Up, new Command() {
            @Override
            public void execute() {
                folder.moveUpItem(CEntityFolderItem.this);
            }
        });
        setActionCommand(ActionType.Down, new Command() {
            @Override
            public void execute() {
                folder.moveDownItem(CEntityFolderItem.this);
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

            CEntityFolder<E> parent = ((CEntityFolder<E>) getParent());
            int index = parent.getItemIndex(CEntityFolderItem.this);

            first = index == 0;
            last = index == parent.getItemCount() - 1;

            CEntityFolderItem<?> previousSibling = parent.getItem(index - 1);
            CEntityFolderItem<?> nextSibling = parent.getItem(index + 1);

            actionsBar.setDefaultActionsState(removable && parent.isRemovable(), movable && !first && previousSibling.isMovable() && parent.isOrderable(),
                    movable && !last && nextSibling.isMovable() && parent.isOrderable());
        }

    }

    @Override
    protected void setComponentsValue(E entity, boolean fireEvent, boolean populate) {
        entityForm.setValue(entity, fireEvent, populate);
    }

    @Override
    public Collection<CComponent<?, ?>> getComponents() {
        if (entityForm == null) {
            return null;
        }
        return Arrays.asList(new CComponent<?, ?>[] { entityForm });
    }

}
