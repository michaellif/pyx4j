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

import com.pyx4j.client.Message;
import com.pyx4j.client.entity.DomainComponentsFactory;
import com.pyx4j.client.exceptions.ClientException;
import com.pyx4j.domain.Entity;

public class DataTableConverterAdapter<E extends Entity> implements DataTableConverter<E> {

    private final DomainComponentsFactory<E> factory;

    public DataTableConverterAdapter(DomainComponentsFactory<E> factory) {
        this.factory = factory;
    }

    public String[][] convert(List<E> list) {
        String[][] data = new String[][] {};
        try {
            List<ColumnDescriptor<E>> descriptors = factory.getColumnDescriptors();
            data = new String[list.size()][descriptors.size()];
            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j < descriptors.size(); j++) {
                    data[i][j] = descriptors.get(j).convert(list.get(i));
                }
            }
        } catch (Exception e) {
            Message.error("Data conversion failed", e);
            throw new ClientException("Data conversion failed", e);
        }
        return data;
    }
}
