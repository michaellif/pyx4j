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
package com.pyx4j.site.client.ui.crud.lister;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.rpc.CrudAppPlace;

public abstract class ListerBase<E extends IEntity> extends AbstractLister<E> implements IListerView<E> {

    public interface ItemSelectionHandler<E> {
        void onSelect(E selectedItem);
    }

    protected Presenter presenter;

    private Class<? extends CrudAppPlace> itemOpenPlaceClass;

    private boolean openEditor;

    public ListerBase(Class<E> clazz) {
        super(clazz);

    }

    public ListerBase(Class<E> clazz, Class<? extends CrudAppPlace> itemOpenPlaceClass, boolean openEditor, boolean allowAddNew) {
        super(clazz, itemOpenPlaceClass != null, allowAddNew);

        this.itemOpenPlaceClass = itemOpenPlaceClass;
        this.openEditor = openEditor;

    }

    // Actions:
    /**
     * Override in derived class for your own select item procedure.
     */
    @Override
    protected void onItemSelect(E item) {
        if (itemOpenPlaceClass != null && openEditor) {
            getPresenter().edit(itemOpenPlaceClass, item.getPrimaryKey());
        } else {
            getPresenter().view(itemOpenPlaceClass, item.getPrimaryKey());
        }
    }

    /**
     * Override in derived class for your own new item creation procedure.
     */
    @Override
    protected void onItemNew() {
        getPresenter().editNew(itemOpenPlaceClass, null);
    }

// IListerView implementation:

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Presenter getPresenter() {
        return presenter;
    }

    @Override
    public ListerBase<E> getLister() {
        return this;
    }

    @Override
    public void onDeleted(Key itemID, boolean isSuccessful) {
        // TODO Auto-generated method stub
    }

    @Override
    public void obtain(int pageNumber) {
        getPresenter().retrieveData(pageNumber);
    }

}
