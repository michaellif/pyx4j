/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 18, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

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
import com.pyx4j.site.client.ui.crud.lister.BasicLister;
import com.pyx4j.site.client.ui.crud.lister.ListerDataSource;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

public abstract class SelectEntityFromListDialog<E extends IEntity> extends OkCancelDialog {

    private final Class<E> entityClass;

    private final boolean isMultiselect;

    private final SelectEntityLister lister;

    private final ListerDataSource<E> dataSource;

    private final List<E> alreadySelected;

    public SelectEntityFromListDialog(Class<E> entityClass, boolean isMultiselect, List<E> alreadySelected, String caption) {
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

            setColumnDescriptors(SelectEntityFromListDialog.this.defineColumnDescriptors());
        }

    }

}
