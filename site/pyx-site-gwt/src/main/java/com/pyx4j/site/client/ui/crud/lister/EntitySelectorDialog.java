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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.DataTable.CheckSelectionHandler;
import com.pyx4j.entity.client.ui.datatable.DataTable.ItemSelectionHandler;
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
        this.lister = new SelectEntityLister(this.entityClass) {
            @Override
            protected void onObtainSuccess() {
                super.onObtainSuccess();
                EntitySelectorDialog.super.show();
            }
        };
        if (this.isMultiselect) {
            lister.getDataTablePanel().getDataTable().addCheckSelectionHandler(new CheckSelectionHandler() {
                @Override
                public void onCheck(boolean isAnyChecked) {
                    getOkButton().setEnabled(isAnyChecked);
                }
            });
        } else {
            lister.getDataTablePanel().getDataTable().addItemSelectionHandler(new ItemSelectionHandler() {
                @Override
                public void onSelect(int selectedRow) {
                    getOkButton().setEnabled(lister.getSelectedItem() != null);
                }
            });
        }
        dataSource = new ListerDataSource<E>(entityClass, getSelectService());
        setFilters(createRestrictionFilterForAlreadySelected());
        lister.setDataSource(dataSource);

        setBody(createBody());
    }

    @Override
    public void show() {
        lister.obtain(0); // populate lister...
        // super.show() will be called in lister.onObtainSuccess() 
    }

    protected abstract List<ColumnDescriptor> defineColumnDescriptors();

    protected abstract AbstractListService<E> getSelectService();

    protected Widget createBody() {
        getOkButton().setEnabled(!lister.getCheckedItems().isEmpty());
        VerticalPanel vPanel = new VerticalPanel();
        vPanel.add(lister.asWidget());
        vPanel.setWidth("100%");
        return vPanel;
    }

    @SuppressWarnings("unchecked")
    protected List<E> getSelectedItems() {
        if (isMultiselect) {
            return lister.getCheckedItems();
        } else {
            E item = lister.getSelectedItem();
            return item == null ? Collections.EMPTY_LIST : Arrays.asList(item);
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

    protected void addFilter(DataTableFilterData filter) {
        dataSource.addPreDefinedFilter(filter);
    }

    protected void addFilters(List<DataTableFilterData> filters) {
        dataSource.addPreDefinedFilters(filters);
    }

    /**
     * Called from within constructor.
     * In order to add additional filters - overwrite it in your class
     * and use addFilter(s) AFTER call to super.setFilters(filters)!..
     * 
     * @param filters
     */
    protected void setFilters(List<DataTableFilterData> filters) {
        dataSource.setPreDefinedFilters(filters);
    }

    private class SelectEntityLister extends BasicLister<E> {

        public SelectEntityLister(Class<E> clazz) {
            super(clazz);
            if (EntitySelectorDialog.this.isMultiselect) {
                setHasCheckboxColumn(true);
            } else {
                setSelectable(true);
            }

            setColumnDescriptors(EntitySelectorDialog.this.defineColumnDescriptors());
        }

    }

}