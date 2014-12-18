/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 21, 2014
 * @author arminea
 */
package com.propertyvista.crm.client.ui.crud.communication.selector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.DataItem;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemSelectionHandler;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;
import com.pyx4j.forms.client.ui.datatable.ListerDataSource;

public class EntityLister<E extends IEntity> extends DataTablePanel<E> {

    private final Collection<E> alreadySelected;

    private final Collection<E> selectedOnTab;

    private final SelectRecipientsDialogForm parent;

    private final Class<E> entityClass;

    public EntityLister(Class<E> clazz, AbstractListCrudService<E> service, SelectRecipientsDialogForm parent, Collection<E> alreadySelected) {
        super(clazz);
        this.entityClass = clazz;
        this.parent = parent;
        this.selectedOnTab = new ArrayList<E>();
        this.alreadySelected = (alreadySelected != null ? alreadySelected : new ArrayList<E>());

        setPageSizeOptions(Arrays.asList(new Integer[] { DataTablePanel.PAGESIZE_SMALL, DataTablePanel.PAGESIZE_MEDIUM }));

        setDataSource(new ListerDataSource<E>(entityClass, service));

        addItemSelectionHandler(new ItemSelectionHandler() {

            @Override
            public void onChange() {
                identifySelected();
            }
        });
    }

    @Override
    public List<Sort> getDefaultSorting() {
        List<Sort> sort = new ArrayList<Sort>();
        return sort;
    }

    protected List<Criterion> createRestrictionFilterForAlreadySelected() {
        List<Criterion> restrictAlreadySelected = new ArrayList<>(alreadySelected.size());

        E proto = EntityFactory.getEntityPrototype(entityClass);

        for (E entity : alreadySelected) {
            restrictAlreadySelected.add(PropertyCriterion.ne(proto.id(), entity.getPrimaryKey()));
        }

        return restrictAlreadySelected;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();
        setRowsSelected();
        setOnTabSelected();
    }

    private void setOnTabSelected() {
        selectedOnTab.clear();
        for (DataItem<E> dataItem : getDataTable().getDataTableModel().getSelectedRows()) {
            selectedOnTab.add(dataItem.getEntity());
        }
    }

    public void setRowsSelected() {

        if (alreadySelected == null || alreadySelected.size() == 0)
            return;
        DataTableModel<E> model = getDataTable().getDataTableModel();

        for (DataItem<E> dataItem : model.getData()) {
            if (alreadySelected.contains(dataItem.getEntity())) {
                model.selectRow(true, model.indexOf(dataItem));
            }
        }
    }

    private void identifySelected() {
        Collection<E> changed = new ArrayList<E>();

        for (DataItem<E> dataItem : getDataTable().getDataTableModel().getSelectedRows()) {
            changed.add(dataItem.getEntity());
        }

        if (changed.size() > selectedOnTab.size()) {
            changed.removeAll(selectedOnTab);
            addItems((changed));
        } else {
            selectedOnTab.removeAll(changed);
            removeItems(new ArrayList<E>(selectedOnTab));
            selectedOnTab.clear();
            selectedOnTab.addAll(changed);
        }
    }

    private void addItems(Collection<E> addItems) {
        selectedOnTab.addAll(addItems);
        alreadySelected.addAll(addItems);
        parent.addSelected((Collection<IEntity>) addItems);
    }

    private void removeItems(Collection<E> removeItems) {
        alreadySelected.removeAll(removeItems);
        parent.removeSelected((Collection<IEntity>) removeItems, entityClass);
    }

}
