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

import java.util.List;

import com.pyx4j.entity.shared.IEntity;

public class DataTableConverterAdapter<E extends IEntity<IEntity<?>>> implements DataTableConverter<E> {

    private final List<ColumnDescriptor<E>> columnDescriptors;

    public DataTableConverterAdapter(List<ColumnDescriptor<E>> columnDescriptors) {
        this.columnDescriptors = columnDescriptors;
    }

    public String[][] convert(List<E> list) {
        String[][] data = new String[][] {};
        data = new String[list.size()][columnDescriptors.size()];
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < columnDescriptors.size(); j++) {
                data[i][j] = columnDescriptors.get(j).convert(list.get(i));
            }
        }
        return data;
    }
}
