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
 * Created on Jan 8, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.serialization.test.client;

import java.io.Serializable;
import java.util.Date;
import java.util.Vector;

import com.pyx4j.commons.EqualsHelper;

@SuppressWarnings("serial")
public class SomeSerializableData implements Serializable {

    private String name;

    private String description;

    private Long longClassValue;

    private long longValue;

    private double doubleValue;

    private Date dateValue;

    private SomeSerializableData objectValue;

    private Vector<SomeSerializableData> objectsVectorValue;

    public Long getLongClassValue() {
        return longClassValue;
    }

    public void setLongClassValue(Long longClassValue) {
        this.longClassValue = longClassValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public SomeSerializableData getObjectValue() {
        return objectValue;
    }

    public void setObjectValue(SomeSerializableData objectValue) {
        this.objectValue = objectValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Vector<SomeSerializableData> getObjectsVectorValue() {
        return objectsVectorValue;
    }

    public void setObjectsVectorValue(Vector<SomeSerializableData> objectsVectorValue) {
        this.objectsVectorValue = objectsVectorValue;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((longValue == 0) || (other == null) || !(other instanceof SomeSerializableData)) {
            return false;
        }
        return longValue == ((SomeSerializableData) other).longValue

        && EqualsHelper.equals(name, ((SomeSerializableData) other).name);
    }

}
