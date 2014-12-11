/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Dec 8, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.datatable.filter;

import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;

public class FilterItem implements Comparable<FilterItem> {

    private final ColumnDescriptor columnDescriptor;

    private final boolean removable;

    public FilterItem(ColumnDescriptor columnDescriptor) {
        this.columnDescriptor = columnDescriptor;
        this.removable = !columnDescriptor.isFilterAlwaysShown();
    }

    public boolean isRemovable() {
        return removable;
    }

    public ColumnDescriptor getColumnDescriptor() {
        return this.columnDescriptor;
    }

    @Override
    public int compareTo(FilterItem o) {
        return 0;
    }

    @Override
    public String toString() {
        return columnDescriptor.getColumnTitle() + " - " + "All";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        return columnDescriptor == ((FilterItem) obj).columnDescriptor;
    }

    @Override
    public int hashCode() {
        return 31 + ((columnDescriptor == null) ? 0 : columnDescriptor.hashCode());
    }

}
