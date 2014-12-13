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
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.datatable;

import com.google.gwt.safehtml.shared.SafeHtml;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.IEntity;

public class ColumnDescriptor {

    public static final String DEFAULT_WIDTH = "100px";

    private final Builder builder;

    public ColumnDescriptor(String columnName, String columnTitle) {
        this(new Builder(columnName, columnTitle));
    }

    protected ColumnDescriptor(Builder builder) {
        this.builder = builder;
    }

    public String getColumnName() {
        return builder.columnName;
    }

    public boolean isSearchable() {
        return builder.searchable;
    }

    public void setSearchable(boolean searchable) {
        builder.searchable = searchable;
    }

    public boolean isFilterAlwaysShown() {
        return builder.filterAlwaysShown;
    }

    public boolean isSortable() {
        return builder.sortable;
    }

    public void setSortable(boolean sortable) {
        builder.sortable = sortable;
    }

    public boolean isSearchableOnly() {
        return builder.searchableOnly;
    }

    public String getColumnTitle() {
        return builder.columnTitle;
    }

    public void setColumnTitle(String columnTitle) {
        builder.columnTitle = columnTitle;
    }

    public String getWidth() {
        return builder.width;
    }

    public void setWidth(String width) {
        builder.width = width;
    }

    public boolean isVisible() {
        return builder.visible;
    }

    public void setVisible(boolean visible) {
        builder.visible = visible;
    }

    public IFormatter<IEntity, SafeHtml> getFormatter() {
        return builder.formatter;
    }

    public void setFormatter(IFormatter<IEntity, SafeHtml> formatter) {
        builder.formatter = formatter;
    }

    protected Builder getBuilder() {
        return builder;
    }

    @Override
    public String toString() {
        return builder.columnName + "[" + builder.columnTitle + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if ((builder.columnName != null) && (obj instanceof ColumnDescriptor)) {
            return builder.columnName.equals(((ColumnDescriptor) obj).builder.columnName);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (builder.columnName != null) {
            return builder.columnName.hashCode();
        } else {
            return super.hashCode();
        }
    }

    public static class Builder {

        private String columnName;

        private String columnTitle;

        private boolean sortable = true;

        private boolean searchable = true;

        private boolean filterAlwaysShown = false;

        private boolean searchableOnly = false;

        private String width = DEFAULT_WIDTH;

        private boolean visible = true;

        IFormatter<IEntity, SafeHtml> formatter;

        public Builder(String columnName, String columnTitle) {
            if (columnName == null) {
                throw new IllegalArgumentException("columnName can't be null");
            }
            this.columnName = columnName;
            this.columnTitle = columnTitle;
        }

        public Builder columnName(String columnName) {
            this.columnName = columnName;
            return this;
        }

        public Builder columnTitle(String columnTitle) {
            this.columnTitle = columnTitle;
            return this;
        }

        public Builder title(String columnTitle) {
            this.columnTitle = columnTitle;
            return this;
        }

        public Builder titlePrefix(String columnTitlePrefix) {
            this.columnTitle = columnTitlePrefix + " " + columnTitle;
            return this;
        }

        public Builder sortable(boolean sortable) {
            this.sortable = sortable;
            return this;
        }

        public Builder searchable(boolean searchable) {
            this.searchable = searchable;
            return this;
        }

        public Builder filterAlwaysShown(boolean filterAlwaysShown) {
            this.filterAlwaysShown = filterAlwaysShown;
            return this;
        }

        public Builder searchableOnly() {
            this.searchable = true;
            this.searchableOnly = true;
            this.visible = false;
            return this;
        }

        public Builder displayOnly() {
            sortable(false);
            searchable(false);
            return this;
        }

        public Builder width(String width) {
            this.width = width;
            return this;
        }

        public Builder visible(boolean visible) {
            this.visible = visible;
            return this;
        }

        public Builder formatter(IFormatter<IEntity, SafeHtml> formatter) {
            this.formatter = formatter;
            return this;
        }

        public ColumnDescriptor build() {
            return new ColumnDescriptor(this);
        }
    }

}
