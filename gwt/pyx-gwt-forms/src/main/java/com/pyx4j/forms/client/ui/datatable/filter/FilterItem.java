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

import com.pyx4j.commons.ConverterUtils;
import com.pyx4j.commons.ConverterUtils.ToStringConverter;
import com.pyx4j.commons.IStringView;
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
        String valueToString = "";
        if (criterion == null || criterion.isEmpty()) {
            valueToString = i18n.tr("All");
        } else if (criterion instanceof PropertyCriterion) {
            Serializable value = ((PropertyCriterion) criterion).getValue();
            if (value instanceof Collection) {
                @SuppressWarnings("unchecked")
                Collection<Object> valuesCollection = (Collection<Object>) value;
                if (valuesCollection.size() == 0) {
                    valueToString = i18n.tr("None");
                } else if (!columnDescriptor.getMemeber().getValueClass().equals(Boolean.class)) {
                    valueToString = ConverterUtils.convertCollection(valuesCollection, new ToStringConverter<Object>() {

                        @Override
                        public String toString(Object value) {
                            if (value instanceof IStringView) {
                                return ((IStringView) value).getStringView();
                            } else {
                                return value.toString();
                            }
                        }

                    }, ",");
                } else {
                    valueToString = ConverterUtils.convertCollection(valuesCollection, new ToStringConverter<Object>() {

                        @Override
                        public String toString(Object value) {
                            if (value == null) {
                                return i18n.tr("Empty");
                            } else if (value.equals(Boolean.TRUE)) {
                                return i18n.tr("Yes");
                            } else {
                                return i18n.tr("No");
                            }
                        }

                    }, ",");
                }
            } else {
                if (value instanceof IStringView) {
                    valueToString = ((IStringView) value).getStringView();
                } else {
                    valueToString = value.toString();
                }
            }
        } else if (criterion instanceof RangeCriterion) {
            Serializable fromValue = ((RangeCriterion) criterion).getFromValue();
            Serializable toValue = ((RangeCriterion) criterion).getToValue();
            if (toValue == null) {
                valueToString = fromValue.toString();
            } else if (fromValue == null) {
                valueToString = toValue.toString();
            } else {
                valueToString = fromValue.toString() + " - " + toValue.toString();
            }
        } else {
            //TODO what else?
        }
        return columnDescriptor.getColumnTitle() + ": " + valueToString;
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
