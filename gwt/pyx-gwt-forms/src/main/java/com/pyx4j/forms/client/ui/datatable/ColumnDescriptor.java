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

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.entity.shared.IEntity;

public class ColumnDescriptor {

    public static final String DEFAULT_WIDTH = "100px";

    private final Builder builder;

    public ColumnDescriptor(String columnName, String columnTitle) {
        this(columnName, columnTitle, DEFAULT_WIDTH);
    }

    public ColumnDescriptor(String columnName, String columnTitle, String width) {
        this(columnName, columnTitle, true, width);
    }

    public ColumnDescriptor(String columnName, String columnTitle, boolean sortable) {
        this(columnName, columnTitle, sortable, DEFAULT_WIDTH);
    }

    public ColumnDescriptor(String columnName, String columnTitle, boolean sortable, String width) {
        this(new Builder(columnName, columnTitle).sortable(sortable).width(width));
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

    public boolean isWordWrap() {
        return builder.wordWrap;
    }

    public void setWordWrap(boolean wordWrap) {
        builder.wordWrap = wordWrap;
    }

    public boolean isVisible() {
        return builder.visible;
    }

    public void setVisible(boolean visible) {
        builder.visible = visible;
    }

    protected Builder getBuilder() {
        return builder;
    }

    //TODO should be abstract
    public String convert(IEntity entity) {
        return entity.toString();
    }

    /**
     * May return Widget to be shown in DataTable
     */
    @SuppressWarnings("deprecation")
    public Object getCellValue(IEntity entity) {
        String text = convert(entity);

        //It is OK to move this formating functions to better place and make algorithms pluggable.

        // hard value trim
        if ((builder.trimLength > 0) && (text != null) && text.length() > builder.trimLength) {
            text = text.substring(0, builder.trimLength) + "...";
        }

        //Break the long words and text
        if ((builder.ensureWordWrap > 0) && (text != null)) {
            StringBuilder b = new StringBuilder();
            int wordLen = 0;
            for (char c : text.toCharArray()) {
                b.append(c);
                wordLen++;
                if (c == '.') {
                    b.append(HtmlUtils.ZERO_WIDTH_SPACE_UTF8);
                    wordLen = 0;
                } else if (Character.isSpace(c)) {
                    wordLen = 0;
                }
                if (wordLen > builder.ensureWordWrap) {
                    b.append(HtmlUtils.ZERO_WIDTH_SPACE_UTF8);
                    wordLen = 0;
                }
            }
            text = b.toString();
        }

        return text;
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

        private boolean searchableOnly = false;

        private String width;

        private boolean wordWrap = true;

        private boolean visible = true;

        private int trimLength = 0;

        private int ensureWordWrap = 0;

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

        public Builder sortable(boolean sortable) {
            this.sortable = sortable;
            return this;
        }

        public Builder searchable(boolean searchable) {
            this.searchable = searchable;
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

        public Builder wordWrap(boolean wordWrap) {
            this.wordWrap = wordWrap;
            return this;
        }

        /**
         * Terminate string with "..." if one exceeds specified length
         */
        public Builder trim(int trimLength) {
            this.trimLength = trimLength;
            return this;
        }

        /**
         * make long text wrappable by html
         * e.g. Something that actually working instead of CSS word-wrap:break-word
         */
        public Builder ensureWordWrap(int maxNonWrappableWordLenght) {
            this.ensureWordWrap = maxNonWrappableWordLenght;
            return this;
        }

        public Builder visible(boolean visible) {
            this.visible = visible;
            return this;
        }

        public ColumnDescriptor build() {
            return new ColumnDescriptor(this);
        }
    }

}
