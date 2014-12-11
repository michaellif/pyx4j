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
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.datatable.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.InMemeoryListService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataItem;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemSelectionHandler;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;
import com.pyx4j.forms.client.ui.datatable.ListerDataSource;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;

public class FilterItemLister<E extends IEntity> extends DataTablePanel<FilterItemDTO> {

    private final Collection<FilterItemDTO> alreadySelected;

    private final Collection<FilterItemDTO> selectedOnTab;

    private final SelectFilterItemDialogForm parent;

    public FilterItemLister(DataTablePanel<?> dataTablePanel, SelectFilterItemDialogForm parent, Collection<FilterItem> alreadySelected) {
        super(FilterItemDTO.class);
        this.parent = parent;
        this.selectedOnTab = new ArrayList<FilterItemDTO>();
        this.alreadySelected = (alreadySelected != null ? convertToFilterItemDTOList(alreadySelected) : new ArrayList<FilterItemDTO>());

        setPageSizeOptions(Arrays.asList(new Integer[] { DataTablePanel.PAGESIZE_SMALL, DataTablePanel.PAGESIZE_MEDIUM }));

        setColumnDescriptors( //
        new MemberColumnDescriptor.Builder(proto().columnDescriptor()).build());
        setDataTableModel();
        setDataSource(dataTablePanel.getDataTable().getColumnDescriptors());

        addItemSelectionHandler(new ItemSelectionHandler() {

            @Override
            public void onChange() {
                identifySelected();
            }
        });
    }

    private void setDataTableModel() {
        DataTableModel<FilterItemDTO> dataTableModel = new DataTableModel<FilterItemDTO>();
        dataTableModel.setPageSize(DataTablePanel.PAGESIZE_SMALL);
        dataTableModel.setMultipleSelection(true);
        setDataTableModel(dataTableModel);
    }

    public void setDataSource(Collection<ColumnDescriptor> columnDescriptors) {
        Collection<FilterItemDTO> filterItems = new ArrayList<FilterItemDTO>();
        for (ColumnDescriptor cd : columnDescriptors) {
            if (cd.isSearchable() && !cd.isFilterAlwaysShown()) {
                FilterItemDTO item = EntityFactory.create(FilterItemDTO.class);
                item.columnDescriptor().setValue(cd.getColumnTitle());
                filterItems.add(item);
            }
        }
        setDataSource(new ListerDataSource<FilterItemDTO>(FilterItemDTO.class, new InMemeoryListService<FilterItemDTO>(filterItems)));
    }

    @Override
    public List<Sort> getDefaultSorting() {
        List<Sort> sort = new ArrayList<Sort>();
        return sort;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();
        setRowsSelected();
        setOnTabSelected();
    }

    private void setOnTabSelected() {
        selectedOnTab.clear();
        for (DataItem<FilterItemDTO> dataItem : getDataTable().getDataTableModel().getSelectedRows()) {
            selectedOnTab.add(dataItem.getEntity());
        }
    }

    public void setRowsSelected() {

        if (alreadySelected == null || alreadySelected.size() == 0)
            return;
        DataTableModel<FilterItemDTO> model = getDataTable().getDataTableModel();

        for (DataItem<FilterItemDTO> dataItem : model.getData()) {
            if (contains(alreadySelected, dataItem.getEntity().columnDescriptor().getValue())) {
                model.selectRow(true, model.indexOf(dataItem));
            }
        }
    }

    public boolean contains(Collection<FilterItemDTO> collection, String entity) {
        for (FilterItemDTO current : collection) {
            if (current.columnDescriptor().getValue().equals(entity)) {
                return true;
            }
        }
        return false;
    }

    private void identifySelected() {
        Collection<FilterItemDTO> changed = new ArrayList<FilterItemDTO>();

        for (DataItem<FilterItemDTO> dataItem : getDataTable().getDataTableModel().getSelectedRows()) {
            changed.add(dataItem.getEntity());
        }

        if (changed.size() > selectedOnTab.size()) {
            changed.removeAll(selectedOnTab);
            addItems((changed));
        } else {
            selectedOnTab.removeAll(changed);
            removeItems(new ArrayList<FilterItemDTO>(selectedOnTab));
            selectedOnTab.clear();
            selectedOnTab.addAll(changed);
        }
    }

    private void addItems(Collection<FilterItemDTO> changed) {
        selectedOnTab.addAll(changed);
        alreadySelected.addAll(changed);
        parent.addSelected(convertToFilterItemList(changed));
    }

    private void removeItems(Collection<FilterItemDTO> removeList) {
        alreadySelected.removeAll(removeList);
        parent.removeSelected(convertToFilterItemList(removeList));
    }

    private Collection<FilterItem> convertToFilterItemList(Collection<FilterItemDTO> from) {
        Collection<FilterItem> to = new ArrayList<FilterItem>(from.size());
        for (FilterItemDTO current : from) {
            to.add(new FilterItem(new ColumnDescriptor(current.columnDescriptor().getValue(), current.columnDescriptor().getValue())));
        }
        return to;
    }

    private Collection<FilterItemDTO> convertToFilterItemDTOList(Collection<FilterItem> from) {
        Collection<FilterItemDTO> to = new ArrayList<FilterItemDTO>(from.size());
        for (FilterItem current : from) {
            FilterItemDTO item = EntityFactory.create(FilterItemDTO.class);
            item.columnDescriptor().setValue(current.getColumnDescriptor().getColumnTitle());
            to.add(item);
        }
        return to;
    }

}
