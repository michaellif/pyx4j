/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * Created on 2011-05-03
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.backoffice.ui.prime.lister;

import java.util.Collection;

import com.google.gwt.user.client.Command;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemZoomInCommand;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.visor.IVisor;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public abstract class AbstractPrimeLister<E extends IEntity> extends EntityDataTablePanel<E> implements IPrimeLister<E> {

    private static final I18n i18n = I18n.get(AbstractPrimeLister.class);

    private Presenter<E> presenter;

    private Class<? extends CrudAppPlace> itemOpenPlaceClass;

    public AbstractPrimeLister(Class<E> clazz) {
        super(clazz);
    }

    public AbstractPrimeLister(Class<E> clazz, boolean allowAddNew) {
        this(clazz, allowAddNew, false);
    }

    public AbstractPrimeLister(Class<E> clazz, boolean allowAddNew, boolean allowDelete) {
        super(clazz, allowAddNew, allowDelete);
        this.itemOpenPlaceClass = AppPlaceEntityMapper.resolvePlaceClass(clazz);
        setItemZoomInCommand(new ItemZoomInCommand<E>() {
            @Override
            public void execute(E item) {
                if (itemOpenPlaceClass != null) {
                    getPresenter().view(itemOpenPlaceClass, item.getPrimaryKey());
                }
            }
        });
    }

    // Actions:

    /**
     * Override in derived class for your own new item creation procedure.
     */
    @Override
    protected void onItemNew() {
        getPresenter().editNew(itemOpenPlaceClass);
    }

    @Override
    protected void onItemsDelete(final Collection<E> items) {
        MessageDialog.confirm(i18n.tr("Confirm"), i18n.tr("Do you really want to delete checked items?"), new Command() {
            @Override
            public void execute() {
                for (E item : items) {
                    getPresenter().delete(item.getPrimaryKey());
                }
            }
        });
    }

    @Override
    protected void onObtainSuccess() {
        updateActionsState();
    }

    protected void updateActionsState() {
        if (getDataTablePanel().getAddButton() != null) {
            getDataTablePanel().getAddButton().setEnabled(getPresenter().canCreateNewItem());
        }
    }

// IListerView implementation:

    @Override
    public void setPresenter(Presenter<E> presenter) {
        this.presenter = presenter;
        if (presenter == null) {
            setDataSource(null);
        } else {
            setDataSource(presenter.getDataSource());
            updateActionsState();
        }
    }

    @Override
    public Presenter<E> getPresenter() {
        return presenter;
    }

    @Override
    public AbstractPrimeLister<E> getLister() {
        return this;
    }

    @Override
    public void onDeleted(Key itemID, boolean isSuccessful) {
        // TODO Auto-generated method stub
    }

    public Class<? extends CrudAppPlace> getItemOpenPlaceClass() {
        return itemOpenPlaceClass;
    }

    @Override
    public void showVisor(IVisor visor) {
        // TODO Auto-generated method stub

    }

    @Override
    public void hideVisor() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isVisorShown() {
        // TODO Auto-generated method stub
        return false;
    }

}
