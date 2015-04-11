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
 * Created on May 8, 2009
 * @author michaellif
 */
package com.pyx4j.forms.client.ui.datatable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.Path;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.CheckGroup;
import com.pyx4j.widgets.client.OptionGroup.Layout;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

public class DataTable<E extends IEntity> implements IsWidget, DataTableModelListener {

    private static final I18n i18n = I18n.get(DataTable.class);

    private ITablePane<E> tablePanel;

    private DataTableModel<E> model;

    private List<ColumnDescriptor> columnDescriptors;

    private boolean hasColumnClickSorting = false;

    private boolean columnSelectorVisible = true;

    private List<ItemSelectionHandler> itemSelectionHandlers;

    private List<SortChangeHandler<E>> sortChangeHandlers;

    private List<ColumnSelectionHandler> columnSelectionHandlers;

    public static interface ItemZoomInCommand<E extends IEntity> {
        void execute(E item);
    }

    public static interface HeaderClickCommand {
        void execute(ColumnDescriptor column);
    }

    public DataTable() {
        if (true) {
            tablePanel = new FlexTablePane<E>(this);
        } else {
            tablePanel = new SectionTablePane<E>(this);
        }
    }

    @Override
    public Widget asWidget() {
        return tablePanel.asWidget();
    }

    public void setColumnDescriptors(List<ColumnDescriptor> columnDescriptors) {
        this.columnDescriptors = columnDescriptors;
    }

    public List<ColumnDescriptor> getColumnDescriptors() {
        return Collections.unmodifiableList(columnDescriptors);
    }

    public List<ColumnDescriptor> getVisibleColumnDescriptors() {
        List<ColumnDescriptor> descriptors = new ArrayList<ColumnDescriptor>();
        for (ColumnDescriptor columnDescriptor : getColumnDescriptors()) {
            if (columnDescriptor.isVisible()) {
                descriptors.add(columnDescriptor);
                continue;
            }
        }
        return descriptors;
    }

    public ColumnDescriptor getColumnDescriptor(int index) {
        return columnDescriptors.get(index);
    }

    public ColumnDescriptor getVisibleColumnDescriptor(int index) {
        int i = -1;
        for (ColumnDescriptor descriptor : columnDescriptors) {
            if (descriptor.isVisible()) {
                i++;
                if (index == i) {
                    return descriptor;
                }
            }
        }
        return null;
    }

    public ColumnDescriptor getColumnDescriptor(Path columnPath) {
        for (ColumnDescriptor descriptor : columnDescriptors) {
            if (descriptor.getColumnPath().equals(columnPath)) {
                return descriptor;
            }
        }
        return null;
    }

    public String getColumnName(int index) {
        return getColumnDescriptor(index).getColumnName();
    }

    public void setItemZoomInCommand(ItemZoomInCommand<E> itemZoomInCommand) {
        tablePanel.setItemZoomInCommand(itemZoomInCommand);
    }

// Data manipulation:

    @Override
    public void onDataTableModelChanged(DataTableModelEvent e) {
        if (e.getType().equals(DataTableModelEvent.Type.REBUILD)) {
            tablePanel.renderTable();
        } else if (e.getType().equals(DataTableModelEvent.Type.SELECTION)) {
            tablePanel.updateSelectionHighlights();
            onRowSelectionChanged();
        }
    }

    public void setDataTableModel(DataTableModel<E> model) {
        DataTableModel<E> previousModel = getDataTableModel();
        if (previousModel != null) {
            previousModel.removeDataTableModelListener(this);
        }
        this.model = model;
        model.addDataTableModelListener(this);
        tablePanel.renderTable();
    }

    public DataTableModel<E> getDataTableModel() {
        return model;
    }

    public E getSelectedItem() {
        Collection<E> selectedItems = getSelectedItems();
        if (selectedItems.size() == 1) {
            return selectedItems.iterator().next();
        } else {
            return null;
        }
    }

    public Collection<E> getSelectedItems() {
        HashSet<E> checked = new HashSet<E>();
        for (DataItem<E> dataItem : model.getSelectedRows()) {
            checked.add(dataItem.getEntity());
        }
        return checked;
    }

// Events connection:

    public void addItemSelectionHandler(ItemSelectionHandler handler) {
        if (itemSelectionHandlers == null) {
            itemSelectionHandlers = new ArrayList<ItemSelectionHandler>(2);
        }
        itemSelectionHandlers.add(handler);
    }

    public void remItemSelectionHandler(ItemSelectionHandler handler) {
        if (itemSelectionHandlers != null) {
            itemSelectionHandlers.remove(handler);
        }
    }

    public void addSortChangeHandler(SortChangeHandler<E> handler) {
        if (sortChangeHandlers == null) {
            sortChangeHandlers = new ArrayList<SortChangeHandler<E>>(2);
        }

        sortChangeHandlers.add(handler);
    }

    public void remSortChangeHandler(SortChangeHandler<E> handler) {
        if (sortChangeHandlers != null) {
            sortChangeHandlers.remove(handler);
        }
    }

    public void addColumnSelectionChangeHandler(ColumnSelectionHandler handler) {
        if (columnSelectionHandlers == null) {
            columnSelectionHandlers = new ArrayList<ColumnSelectionHandler>(2);
        }
        columnSelectionHandlers.add(handler);
    }

    public void remColumnSelectionChangeHandler(ColumnSelectionHandler handler) {
        if (columnSelectionHandlers != null) {
            columnSelectionHandlers.remove(handler);
        }
    }

// UI & behaviour setup:

    public boolean hasColumnClickSorting() {
        return hasColumnClickSorting;
    }

    public void setHasColumnClickSorting(boolean hasColumnClickSorting) {
        this.hasColumnClickSorting = hasColumnClickSorting;
    }

    public boolean isMultipleSelection() {
        return model.isMultipleSelection();
    }

    public boolean isColumnSelectorVisible() {
        return columnSelectorVisible;
    }

    public void setColumnSelectorVisible(boolean columnSelectorVisible) {
        this.columnSelectorVisible = columnSelectorVisible;
    }

    public boolean isItemZoomInAvailable() {
        return tablePanel.isItemZoomInAvailable();
    }

//
// Internals:
//

    public void updateColumnVizibility(int offsetWidth, boolean reduceColumns) {

    }

    void onSortColumnChanged() {
        if (sortChangeHandlers != null) {
            for (SortChangeHandler<?> handler : sortChangeHandlers) {
                handler.onChange();
            }
        }
    }

    void onColumnSelectionChanged() {
        if (columnSelectionHandlers != null) {
            for (ColumnSelectionHandler handler : columnSelectionHandlers) {
                handler.onChange();
            }
        }
    }

    void onRowSelectionChanged() {
        if (itemSelectionHandlers != null) {
            for (ItemSelectionHandler handler : itemSelectionHandlers) {
                handler.onChange();
            }
        }

    }

    protected void showColumnSelectorDialog() {
        new ColumnSelectorDialog().show();
    }

    // Events:
    public interface ItemSelectionHandler {
        void onChange();
    }

    public interface SortChangeHandler<E> {
        void onChange();
    }

    public interface ColumnSelectionHandler {
        void onChange();
    }

    private class ColumnSelectorDialog extends OkCancelDialog {

        private final CheckGroup<ColumnDescriptor> checkGroup;

        public ColumnSelectorDialog() {
            super("Select Columns");
            checkGroup = new CheckGroup<>(Layout.VERTICAL);
            checkGroup.setFormatter(new IFormatter<ColumnDescriptor, SafeHtml>() {

                @Override
                public SafeHtml format(ColumnDescriptor value) {
                    return SafeHtmlUtils.fromTrustedString(value.getColumnTitle());
                }
            });

            checkGroup.setHeight("200px");
            checkGroup.setWidth("100%");

            List<ColumnDescriptor> options = new ArrayList<>();
            List<ColumnDescriptor> value = new ArrayList<>();

            for (ColumnDescriptor column : getColumnDescriptors()) {
                if (!column.isSearchableOnly()) {
                    options.add(column);
                    if (column.isVisible()) {
                        value.add(column);
                    }
                }
            }

            checkGroup.setOptions(options);
            checkGroup.setValue(value);

            setDialogPixelWidth(300);
            setBody(new ScrollPanel(checkGroup));
        }

        @Override
        public boolean onClickOk() {
            Collection<ColumnDescriptor> selection = checkGroup.getValue();
            if (selection.size() == 0) {
                MessageDialog.warn(i18n.tr("Warning"), i18n.tr("Please specify at least one column!"));
                return false;
            }

            boolean hasChanged = false;
            for (ColumnDescriptor column : getColumnDescriptors()) {
                if (!column.isSearchableOnly()) {
                    if (selection.contains(column) ^ column.isVisible()) {
                        column.setVisible(!column.isVisible());
                        hasChanged = true;
                    }
                }
            }

            if (hasChanged) {
                tablePanel.renderTable();
                onColumnSelectionChanged();
            }

            return true;
        }
    }

}
