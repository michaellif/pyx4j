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
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.datatable;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IEntity;

public class DataTableFilterGrid<E extends IEntity> extends FlowPanel {

    private final DataTablePanel<E> dataTablePanel;

    public DataTableFilterGrid(DataTablePanel<E> dataTablePanel) {
        this.dataTablePanel = dataTablePanel;
        setWidth("100%");
    }

    public void addFilter(DataTableFilterItem<E> filter) {
        add(filter);
        filter.setParent(this);
    }

    public void removeFilter(DataTableFilterItem<E> filter) {
        filter.setParent(null);
        remove(filter);
        if (getFilterCount() == 0) {
//            btnApply.setEnabled(false);
//            getPresenter().populate(0);
        }
    }

    public int getFilterCount() {
        return getWidgetCount();
    }

    public DataTablePanel<E> getDataTablePanel() {
        return dataTablePanel;
    }

    @SuppressWarnings("unchecked")
    public List<DataTableFilterData> getFilterData() {
        ArrayList<DataTableFilterData> filters = new ArrayList<DataTableFilterData>();

        for (Widget w : this) {
            if (w instanceof DataTableFilterItem) {
                filters.add(((DataTableFilterItem<E>) w).getFilterData());
            }
        }

        return filters;
    }

    public void setFiltersData(List<DataTableFilterData> filterData) {
        clear();
        if (filterData != null) {
            DataTableFilterItem<E> filter;
            for (DataTableFilterData item : filterData) {
                addFilter(filter = new DataTableFilterItem<E>(this));
                filter.setFilterData(item);
            }
        }
    }

}
