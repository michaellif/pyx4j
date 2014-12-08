/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.util;

import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTable.ColumnSelectionHandler;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemZoomInCommand;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;

import com.propertyvista.domain.dashboard.gadgets.util.ColumnUserSettings;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;

public class ListerUtils {

    public static class DataTablePanelInitializer<E extends IEntity> {

        private List<ColumnDescriptor> columnDescriptors;

        private Boolean isSetupable;

        private Provider<ListerUserSettings> userSettingsProvider;

        private Command onColumnSelectionChanged;

        private ItemZoomInCommand<E> itemZoomInCommand;

        private final DataTablePanel<E> dataTablePanel;

        public DataTablePanelInitializer(DataTablePanel<E> dataTablePanel) {
            this.dataTablePanel = dataTablePanel;
            itemZoomInCommand = null;
        }

        public DataTablePanelInitializer<E> columnDescriptors(List<ColumnDescriptor> columnDescriptors) {
            this.columnDescriptors = columnDescriptors;
            return this;
        }

        public DataTablePanelInitializer<E> setupable(boolean isSetupable) {
            this.isSetupable = isSetupable;
            return this;
        }

        public DataTablePanelInitializer<E> userSettingsProvider(Provider<ListerUserSettings> userSettingsProvider) {
            this.userSettingsProvider = userSettingsProvider;
            return this;
        }

        public DataTablePanelInitializer<E> onColumnSelectionChanged(Command command) {
            this.onColumnSelectionChanged = command;
            return this;
        }

        public DataTablePanelInitializer<E> itemZoomInCommand(ItemZoomInCommand<E> command) {
            this.itemZoomInCommand = command;
            return this;
        }

        public void init() {
            assert dataTablePanel != null;
            assert columnDescriptors != null;
            assert isSetupable != null;
            assert userSettingsProvider != null;
            assert onColumnSelectionChanged != null;

            dataTablePanel.setColumnDescriptors(ColumnDescriptorDiffUtil.applyDiff((Class<E>) dataTablePanel.proto().getInstanceValueClass(),
                    columnDescriptors, userSettingsProvider.get()));

            DataTableModel<E> dataTableModel = new DataTableModel<E>();
            dataTableModel.setMultipleSelection(false);
            dataTableModel.setPageSize(userSettingsProvider.get().pageSize().isNull() ? 10 : userSettingsProvider.get().pageSize().getValue());
            dataTablePanel.setDataTableModel(dataTableModel);

            dataTablePanel.getDataTable().addColumnSelectionChangeHandler(new ColumnSelectionHandler() {
                @Override
                public void onChange() {
                    List<ColumnUserSettings> diff = ColumnDescriptorDiffUtil.getDescriptorsDiff(columnDescriptors, dataTablePanel.getDataTable()
                            .getColumnDescriptors());
                    userSettingsProvider.get().overriddenColumns().clear();
                    userSettingsProvider.get().overriddenColumns().addAll(diff);

                    onColumnSelectionChanged.execute();
                }
            });

            dataTablePanel.getDataTable().setItemZoomInCommand(itemZoomInCommand);
            dataTablePanel.getDataTable().setColumnSelectorVisible(isSetupable);
            dataTablePanel.setFilteringEnabled(false);
            dataTablePanel.setPageSizeOptions(null); // turn off page size selection control 
            dataTablePanel.getDataTable().setHasColumnClickSorting(true);

        }
    }

    public static <E extends IEntity> DataTablePanelInitializer<E> bind(DataTablePanel<E> dataTablePanel) {
        return new DataTablePanelInitializer<E>(dataTablePanel);
    }

}
