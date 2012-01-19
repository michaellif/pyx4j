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
 * Created on Jan 19, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.crud.lister;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.DataTable.CheckSelectionHandler;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData.Operators;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

public abstract class EntitySelectorDialog<E extends IEntity> extends OkCancelDialog {

    private final Class<E> entityClass;

    private final boolean isMultiselect;

    private final SelectEntityLister lister;

    private final ListerDataSource<E> dataSource;

    private final List<E> alreadySelected;

    public EntitySelectorDialog(Class<E> entityClass, boolean isMultiselect, List<E> alreadySelected, String caption) {
        super(caption);
        this.entityClass = entityClass;
        this.isMultiselect = isMultiselect;
        this.alreadySelected = new ArrayList<E>(alreadySelected);
        lister = new SelectEntityLister(this.entityClass, this.isMultiselect);
        dataSource = new ListerDataSource<E>(entityClass, getSelectService());
        setPreDefinedFilters(new LinkedList<DataTableFilterData>());

        lister.setDataSource(dataSource);
        lister.obtain(0);

        setBody(createBody());
        setSize(width(), height());
    }

    protected abstract String width();

    protected abstract String height();

    protected abstract ColumnDescriptor<?>[] defineColumnDescriptors();

    protected abstract AbstractListService<E> getSelectService();

    protected Widget createBody() {
        getOkButton().setEnabled(!lister.getCheckedItems().isEmpty());
        lister.getDataTablePanel().getDataTable().addCheckSelectionHandler(new CheckSelectionHandler() {

            @Override
            public void onCheck(boolean isAnyChecked) {
                getOkButton().setEnabled(isAnyChecked);
            }
        });

        VerticalPanel vPanel = new VerticalPanel();
        vPanel.add(lister.asWidget());
        vPanel.setWidth("100%");
        return vPanel;
    }

    protected List<E> getSelectedItems() {
        if (isMultiselect) {
            return lister.getCheckedItems();
        } else {
            return lister.getSelectedItems();
        }
    }

    protected E proto() {
        return EntityFactory.getEntityPrototype(entityClass);
    }

    protected List<DataTableFilterData> createRestrictionFilterForAlreadySelected() {
        List<DataTableFilterData> restrictAlreadySelected = new ArrayList<DataTableFilterData>(alreadySelected.size());

        E proto = EntityFactory.getEntityPrototype(entityClass);

        for (E entity : alreadySelected) {
            restrictAlreadySelected.add(new DataTableFilterData(proto.id().getPath(), Operators.isNot, entity.id().getValue()));
        }

        return restrictAlreadySelected;
    }

    protected void setPreDefinedFilters(List<DataTableFilterData> preDefinedFilters) {
        preDefinedFilters = new LinkedList<DataTableFilterData>(preDefinedFilters);
        preDefinedFilters.addAll(createRestrictionFilterForAlreadySelected());
        dataSource.setPreDefinedFilters(preDefinedFilters);
    }

    private class SelectEntityLister extends BasicLister<E> {

        public SelectEntityLister(Class<E> clazz, boolean isMultiselect) {
            super(clazz);
            setHasCheckboxColumn(isMultiselect);

            setColumnDescriptors(EntitySelectorDialog.this.defineColumnDescriptors());
        }

    }

}