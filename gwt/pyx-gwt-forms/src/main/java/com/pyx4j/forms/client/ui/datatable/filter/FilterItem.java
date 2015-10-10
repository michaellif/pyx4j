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
 */
package com.pyx4j.forms.client.ui.datatable.filter;

import java.io.Serializable;
import java.util.Collection;

import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.RangeCriterion;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;

public class FilterItem implements Comparable<FilterItem> {

    private static final I18n i18n = I18n.get(FilterItem.class);

    private final ColumnDescriptor columnDescriptor;

    private Criterion criterion;

    private final boolean removable;

    private boolean editorShownOnAttach;

    public FilterItem(ColumnDescriptor columnDescriptor) {
        this(columnDescriptor, false);
    }

    public FilterItem(ColumnDescriptor columnDescriptor, boolean editorShownOnAttach) {
        this.columnDescriptor = columnDescriptor;
        this.editorShownOnAttach = editorShownOnAttach;
        this.removable = !columnDescriptor.isFilterAlwaysShown();
    }

    public boolean isRemovable() {
        return removable;
    }

    public ColumnDescriptor getColumnDescriptor() {
        return this.columnDescriptor;
    }

    public Criterion getCriterion() {
        return criterion;
    }

    public void setCriterion(Criterion criterion) {
        this.criterion = criterion;
    }

    @Override
    public int compareTo(FilterItem o) {
        return 0;
    }

    @Override
    public String toString() {
        if (criterion == null) {
            return columnDescriptor.getColumnTitle() + ": '" + i18n.tr("All") + "'";
        } else if (criterion instanceof PropertyCriterion) {
            Serializable value = ((PropertyCriterion) criterion).getValue();
            if (value instanceof Collection) {
                if (((Collection) value).size() == 0) {
                    return columnDescriptor.getColumnTitle() + ": 'None'";
                }
                if (!columnDescriptor.getMemeber().getValueClass().equals(Boolean.class)) {
                    return columnDescriptor.getColumnTitle() + ": (" + ((Collection) value).size() + ")";
                } else {
                    StringBuilder selected = new StringBuilder();
                    for (Object val : (Collection) value) {

                        if (val == null) {
                            selected.append(selected.toString().equals("") ? "Empty" : ", Empty");

                        } else if (val.equals(Boolean.TRUE)) {
                            selected.append(selected.toString().equals("") ? "Yes" : ", Yes");
                        } else {
                            selected.append(selected.toString().equals("") ? "No" : ", No");
                        }
                    }
                    return columnDescriptor.getColumnTitle() + ": '" + selected.toString() + "'";
                }
            } else {
                return columnDescriptor.getColumnTitle() + ": '" + ((value == null) ? i18n.tr("All") : value) + "'";
            }
        } else if (criterion instanceof RangeCriterion) {
            Serializable fromValue = ((RangeCriterion) criterion).getFromValue();
            Serializable toValue = ((RangeCriterion) criterion).getToValue();
            return columnDescriptor.getColumnTitle() + ": '"
                    + ((fromValue == null && toValue == null) ? i18n.tr("All")
                            : //
                            ((fromValue == null ? "" : fromValue) + ((fromValue != null && toValue != null) ? "'-'" : "") + //
                                    (toValue == null ? "" : toValue)))
                    + "'";
        } else {
            return columnDescriptor.getColumnTitle();
        }
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

    public boolean isEditorShownOnAttach() {
        return editorShownOnAttach;
    }

    public void setEditorShownOnAttach(boolean editorShownOnAttach) {
        this.editorShownOnAttach = editorShownOnAttach;
    }
}
