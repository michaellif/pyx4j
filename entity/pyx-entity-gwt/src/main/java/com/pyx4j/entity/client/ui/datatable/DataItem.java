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

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.shared.IEntity;

public class DataItem<E extends IEntity> {

    private final Map<ColumnDescriptor<E>, Object> dataMap = new HashMap<ColumnDescriptor<E>, Object>();

    private boolean checked;

    private final DataTableModel<E> model;

    public DataItem(DataTableModel<E> model) {
        this.model = model;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setCellValue(String columnName, Object value) {
        ColumnDescriptor<E> columnDescriptor = model.getColumnDescriptor(columnName);
        if (columnDescriptor == null) {
            throw new IllegalArgumentException("No column with name " + columnName + " is found.");
        }
        dataMap.put(columnDescriptor, value);
    }

    public Object getCellValue(ColumnDescriptor<E> descriptor) {
        return dataMap.get(descriptor);
    }

}
