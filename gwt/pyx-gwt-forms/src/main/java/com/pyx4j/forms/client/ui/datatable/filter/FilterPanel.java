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
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.RangeCriterion;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.OkCancelOption;
import com.pyx4j.widgets.client.selector.ItemHolderFactory;
import com.pyx4j.widgets.client.selector.SelectorListBox;
import com.pyx4j.widgets.client.selector.SelectorListBoxValuePanel;

public class FilterPanel extends SelectorListBox<FilterItem> {

    private static final Logger log = LoggerFactory.getLogger(FilterPanel.class);

    private static final I18n i18n = I18n.get(FilterPanel.class);

    private DataTablePanel<?> dataTablePanel;

    public FilterPanel(final DataTablePanel<?> dataTablePanel) {
        super(new FilterOptionsGrabber(dataTablePanel), new IFormatter<FilterItem, SafeHtml>() {
            @Override
            public SafeHtml format(FilterItem value) {
                SafeHtmlBuilder builder = new SafeHtmlBuilder();
                builder.appendHtmlConstant(SimpleMessageFormat.format("<div style=\"padding:2px;\">{0}</div>", value.getColumnDescriptor().getColumnTitle()));
                return builder.toSafeHtml();
            }
        }, new ItemHolderFactory<FilterItem>() {

            @Override
            public FilterItemHolder createItemHolder(FilterItem item, SelectorListBoxValuePanel<FilterItem> valuePanel) {
                return new FilterItemHolder(item, valuePanel);
            }
        });

        this.dataTablePanel = dataTablePanel;

        addValueChangeHandler(new ValueChangeHandler<Collection<FilterItem>>() {

            @Override
            public void onValueChange(ValueChangeEvent<Collection<FilterItem>> event) {
                dataTablePanel.populate(0);
            }
        });

        setAction(new Command() {
            @Override
            public void execute() {
                final FilterItemAddDialog dialog = new FilterItemAddDialog(FilterPanel.this);

                dialog.setDialogOptions(new OkCancelOption() {

                    @Override
                    public boolean onClickOk() {
                        List<FilterItem> items = new ArrayList<>(getValue());

                        for (ColumnDescriptor cd : getColumnDescriptors()) {
                            if (cd.isSearchable() && !cd.isFilterAlwaysShown()) {
                                FilterItem item = new FilterItem(cd);
                                if (dialog.getSelectedItems().contains(cd) && !items.contains(item)) {
                                    items.add(item);
                                } else if (!dialog.getSelectedItems().contains(cd) && items.contains(item)) {
                                    items.remove(item);
                                }
                            }
                        }
                        if (items.size() > 0) {
                            items.get(items.size() - 1).setEditorShownOnAttach(true);
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

        setWatermark(i18n.tr("+ Add item"));
    }

    public List<ColumnDescriptor> getColumnDescriptors() {
        return dataTablePanel.getDataTable().getColumnDescriptors();
    }

    public void resetFilters() {
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

    public void setFilters(List<Criterion> filters) {
        List<FilterItem> items = new ArrayList<>();
        List<ColumnDescriptor> columnDescriptors = dataTablePanel.getDataTable().getColumnDescriptors();
        for (ColumnDescriptor columnDescriptor : columnDescriptors) {
            if (columnDescriptor.isFilterAlwaysShown()) {
                items.add(new FilterItem(columnDescriptor));
            }
        }
        for (Criterion criterion : filters) {
            Path propertyPath = null;
            if (criterion instanceof PropertyCriterion) {
                propertyPath = ((PropertyCriterion) criterion).getPropertyPath();
            } else if (criterion instanceof RangeCriterion) {
                propertyPath = ((RangeCriterion) criterion).getPropertyPath();
            }
            if (propertyPath != null) {
                boolean columnFound = false;
                for (ColumnDescriptor columnDescriptor : columnDescriptors) {
                    if (propertyPath.equals(columnDescriptor.getColumnPath())) {
                        columnFound = true;
                        FilterItem item = new FilterItem(columnDescriptor);
                        if (items.contains(item)) {
                            items.get(items.indexOf(item)).setCriterion(criterion);
                        } else {
                            item.setCriterion(criterion);
                            items.add(item);
                        }
                        break;
                    }
                }
                if (!columnFound) {
                    if (ApplicationMode.isDevelopment()) {
                        throw new Error("Filter ColumnDescriptor not found for " + criterion);
                    } else {
                        log.error("Filter ColumnDescriptor not found for {}", criterion);
                    }
                }
            }
        }
        setValue(items);
    }

    public List<Criterion> getFilters() {
        Collection<FilterItem> filterItems = getValue();
        List<Criterion> filters = new ArrayList<>();
        for (FilterItem filterItem : filterItems) {
            if (filterItem.getCriterion() != null) {
                if (filterItem.getCriterion() instanceof PropertyCriterion && ((PropertyCriterion) filterItem.getCriterion()).getValue() == null) {
                    continue;
                }
                filters.add(filterItem.getCriterion());
            }
        }
        return filters;
    }

    @Override
    public void setSelection(FilterItem item) {
        item.setEditorShownOnAttach(true);
        super.setSelection(item);
    }

    @Override
    public void removeItem(FilterItem item) {
        super.removeItem(item);
        item.setCriterion(null);
    }

}