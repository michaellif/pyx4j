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

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTable.ColumnSelectionHandler;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemSelectionHandler;
import com.pyx4j.site.client.ui.crud.lister.BasicLister;
import com.pyx4j.site.client.ui.crud.lister.ListerDataSource;

import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;

public class ListerUtils {

    public static interface ItemSelectCommand<E extends IEntity> {

        void execute(E item);

    }

    public static class ListerInitializer<E extends IEntity> {

        private final BasicLister<E> lister;

        private List<ColumnDescriptor> columnDescriptors;

        private AbstractListService<E> service;

        private Boolean isSetupable;

        private Provider<ListerUserSettings> userSettingsProvider;

        private Command onColumnSelectionChanged;

        private ItemSelectCommand<E> onItemSelectedCommand;

        public ListerInitializer(BasicLister<E> lister) {
            this.lister = lister;
            onItemSelectedCommand = null;
        }

        public ListerInitializer<E> columnDescriptors(List<ColumnDescriptor> columnDescriptors) {
            this.columnDescriptors = columnDescriptors;
            return this;
        }

        public ListerInitializer<E> service(AbstractListService<E> service) {
            this.service = service;
            return this;
        }

        public ListerInitializer<E> setupable(boolean isSetupable) {
            this.isSetupable = isSetupable;
            return this;
        }

        public ListerInitializer<E> userSettingsProvider(Provider<ListerUserSettings> userSettingsProvider) {
            this.userSettingsProvider = userSettingsProvider;
            return this;
        }

        public ListerInitializer<E> onColumnSelectionChanged(Command command) {
            this.onColumnSelectionChanged = command;
            return this;
        }

        public ListerInitializer<E> onItemSelectedCommand(ListerUtils.ItemSelectCommand<E> command) {
            this.onItemSelectedCommand = command;
            return this;
        }

        public void init() {
            assert lister != null;
            assert columnDescriptors != null;
            assert service != null;
            assert isSetupable != null;
            assert userSettingsProvider != null;
            assert onColumnSelectionChanged != null;

            lister.setDataSource(new ListerDataSource<E>((Class<E>) lister.proto().getInstanceValueClass(), service));

            lister.getDataTablePanel().setColumnDescriptors(
                    ColumnDescriptorDiffUtil.applyDiff((Class<E>) lister.proto().getInstanceValueClass(), columnDescriptors, userSettingsProvider.get()));
            lister.getDataTablePanel().getDataTable().addColumnSelectionChangeHandler(new ColumnSelectionHandler() {
                @Override
                public void onColumSelectionChanged() {
                    ListerUserSettings diff = ColumnDescriptorDiffUtil.getDescriptorsDiff(columnDescriptors, lister.getDataTablePanel().getDataTable()
                            .getDataTableModel().getColumnDescriptors());
                    userSettingsProvider.get().set(diff);

                    onColumnSelectionChanged.execute();
                }
            });

            lister.getDataTablePanel().getDataTable().setHasDetailsNavigation(onItemSelectedCommand != null);
            if (onItemSelectedCommand != null) {
                lister.getDataTablePanel().getDataTable().addItemSelectionHandler(new ItemSelectionHandler() {

                    @Override
                    public void onSelect(int selectedRow) {
                        E item = lister.getDataTablePanel().getDataTable().getSelectedItem();
                        if (item != null) {
                            onItemSelectedCommand.execute(item);
                        }
                    }
                });
            }

            lister.getDataTablePanel().setPageSize(userSettingsProvider.get().pageSize().isNull() ? 10 : userSettingsProvider.get().pageSize().getValue());
            lister.getDataTablePanel().getDataTable().setColumnSelectorVisible(isSetupable);

            lister.getDataTablePanel().setFilteringEnabled(false);
            lister.getDataTablePanel().setPageSizeOptions(null); // turn off page size selection control 
            lister.getDataTablePanel().getDataTable().setHasColumnClickSorting(true);
            lister.getDataTablePanel().getDataTable().setHasCheckboxColumn(false);
            lister.getDataTablePanel().getDataTable().setMarkSelectedRow(false);
            lister.getDataTablePanel().getDataTable().setAutoColumnsWidth(true);

        }
    }

    public static <E extends IEntity> ListerInitializer<E> bind(BasicLister<E> lister) {
        return new ListerInitializer<E>(lister);
    }

}
