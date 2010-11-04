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
 * Created on 2010-11-04
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.serialization.test.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

@SuppressWarnings("serial")
public class SerializableDataEntity implements Serializable {

    private Long id;

    private String name;

    private Vector<SerializableDataEntity> children;

    public static ArrayList<SerializableDataEntity> generateData(int rows, int columns, long offset, boolean setName) {
        ArrayList<SerializableDataEntity> data = new ArrayList<SerializableDataEntity>();
        long count = offset;
        for (int r = 0; r < rows; r++) {
            SerializableDataEntity rowData = new SerializableDataEntity();
            if (offset < 0) {
                rowData.setId(new Long(r));
            } else {
                rowData.setId(new Long(count++));
            }
            if (setName) {
                rowData.setName("Parent " + String.valueOf(r));
            }
            for (int c = 0; c < columns - 1; c++) {
                SerializableDataEntity colData = new SerializableDataEntity();
                if (offset < 0) {
                    colData.setId(new Long(rows * r + c));
                } else {
                    colData.setId(new Long(count++));
                }
                if (setName) {
                    colData.setName("Member " + String.valueOf(r) + " - " + String.valueOf(c));
                }
                rowData.addChild(colData);
            }
            data.add(rowData);
        }
        return data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vector<SerializableDataEntity> getChildren() {
        return children;
    }

    public void setChildren(Vector<SerializableDataEntity> children) {
        this.children = children;
    }

    public void addChild(SerializableDataEntity child) {
        if (children == null) {
            children = new Vector<SerializableDataEntity>();
        }
        children.add(child);
    }

    public int totalCount() {
        int count = 1;
        if (children != null) {
            for (SerializableDataEntity child : children) {
                count += child.totalCount();
            }
        }
        return count;
    }

    public static int totalCount(ArrayList<SerializableDataEntity> data) {
        int count = 0;
        if (data != null) {
            for (SerializableDataEntity child : data) {
                count += child.totalCount();
            }
        }
        return count;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((id == null) || (other == null) || !(other instanceof SerializableDataEntity) || (!this.getClass().equals(other.getClass()))) {
            return false;
        }
        return (id.equals(((SerializableDataEntity) other).getId()));
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return super.hashCode();
        }
        return id.hashCode() + (this.getClass().hashCode() * 0xFFFF);
    }
}
