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
package com.pyx4j.entity.client.ui.datatable;

public class ColumnDescriptor<E> {

    public static final String DEFAULT_WIDTH = "100px";

    private final String columnName;

    private String columnTitle;

    private boolean sortable;

    private boolean navigable;

    private boolean sortAscending = true;

    private String width;

    private boolean wordWrap = true;

    private final boolean visible = false;

    public ColumnDescriptor(String columnName, String columnTitle) {
        this(columnName, columnTitle, DEFAULT_WIDTH);
    }

    public ColumnDescriptor(String columnName, String columnTitle, String width) {
        this(columnName, columnTitle, true, true, width);
    }

    public ColumnDescriptor(String columnName, String columnTitle, boolean sortable, boolean sortAscending) {
        this(columnName, columnTitle, sortable, sortAscending, DEFAULT_WIDTH);
    }

    public ColumnDescriptor(String columnName, String columnTitle, boolean sortable, boolean sortAscending, String width) {
        if (columnName == null) {
            throw new IllegalArgumentException("columnName can't be null");
        }

        this.columnTitle = columnTitle;
        this.columnName = columnName;
        this.sortable = sortable;
        this.sortAscending = sortAscending;
        this.width = width;

    }

    public String getColumnName() {
        return columnName;
    }

    public boolean isSortAscending() {
        return sortAscending;
    }

    public void setSortAscending(boolean sortAscending) {
        this.sortAscending = sortAscending;
    }

    public boolean isSortable() {
        return sortable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public boolean isNavigable() {
        return navigable;
    }

    public void setNavigable(boolean navigable) {
        this.navigable = navigable;
    }

    public String getColumnTitle() {
        return columnTitle;
    }

    public void setColumnTitle(String columnTitle) {
        this.columnTitle = columnTitle;
    }

    @Override
    public String toString() {
        return columnName + "[" + columnTitle + "]";
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public boolean isWordWrap() {
        return wordWrap;
    }

    public void setWordWrap(boolean wordWrap) {
        this.wordWrap = wordWrap;
    }

    //TODO should be abstract
    public String convert(E entity) {
        return entity.toString();
    }

}
