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
 * Created on Nov 14, 2011
 * @author michaellif
 */
package com.pyx4j.forms.client.ui.datatable.filter;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;
import com.pyx4j.widgets.client.dialog.OkCancelOption;
import com.pyx4j.widgets.client.selector.ItemHolderFactory;
import com.pyx4j.widgets.client.selector.SelectorListBox;
import com.pyx4j.widgets.client.selector.SelectorListBoxValuePanel;

public class FilterPanel extends SelectorListBox<FilterItem> {

    private DataTablePanel<?> dataTablePanel;

    public FilterPanel(DataTablePanel<?> dataTablePanel) {
        super(new FilterOptionsGrabber(dataTablePanel), new IFormatter<FilterItem, SafeHtml>() {
            @Override
            public SafeHtml format(FilterItem value) {
                SafeHtmlBuilder builder = new SafeHtmlBuilder();
                builder.appendHtmlConstant(SimpleMessageFormat.format("<div style=\"padding:2px;\">{0}</div>", value.toString()));
                return builder.toSafeHtml();
            }
        }, new ItemHolderFactory<FilterItem>() {

            @Override
            public FilterItemHolder createItemHolder(FilterItem item, SelectorListBoxValuePanel<FilterItem> valuePanel) {
                return new FilterItemHolder(item, new FilterItemFormatter(), valuePanel);
            }
        });

        this.dataTablePanel = dataTablePanel;

        setAction(new Command() {
            @Override
            public void execute() {
                final FilterItemAddDialog dialog = new FilterItemAddDialog(FilterPanel.this);

                dialog.setDialogOptions(new OkCancelOption() {

                    @Override
                    public boolean onClickOk() {
                        List<FilterItem> items = new ArrayList<>(getValue());
                        for (ColumnDescriptor columnDescriptor : dialog.getSelectedItems()) {
                            FilterItem item = new FilterItem(columnDescriptor);
                            if (!items.contains(item)) {
                                items.add(item);
                            }
                        }
                        FilterPanel.this.setValue(items);
                        return true;
                    }

                    @Override
                    public boolean onClickCancel() {
                        return true;
                    }
                });
                dialog.show();
            }
        });

    }

    public List<ColumnDescriptor> getColumnDescriptors() {
        return dataTablePanel.getDataTable().getColumnDescriptors();
    }

    public void onColimnDescriptorsChanged() {
        ((FilterOptionsGrabber) getOptionsGrabber()).updateFilterOptions();

        List<ColumnDescriptor> columnDescriptors = dataTablePanel.getDataTable().getColumnDescriptors();
        List<FilterItem> alwaysShownItems = new ArrayList<>();
        for (ColumnDescriptor columnDescriptor : columnDescriptors) {
            if (columnDescriptor.isFilterAlwaysShown()) {
                alwaysShownItems.add(new FilterItem(columnDescriptor));
            }
        }
        setValue(alwaysShownItems);
    }
}