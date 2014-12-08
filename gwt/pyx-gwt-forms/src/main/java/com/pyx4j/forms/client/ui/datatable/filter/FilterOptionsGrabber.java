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
package com.pyx4j.forms.client.ui.datatable.filter;

import java.util.ArrayList;

import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;
import com.pyx4j.widgets.client.selector.MultyWordSuggestOptionsGrabber;

public class FilterOptionsGrabber extends MultyWordSuggestOptionsGrabber<FilterItem> {

    public FilterOptionsGrabber(DataTablePanel<?> dataTablePanel) {

        ArrayList<FilterItem> filterItems = new ArrayList<>();

        for (ColumnDescriptor cd : dataTablePanel.getDataTableModel().getColumnDescriptors()) {
            if (cd.isSearchable()) {
                filterItems.add(new FilterItem(cd));
            }
        }

        setFormatter(new FilterItemFormatter());
        setAllOptions(filterItems);
    }

}
