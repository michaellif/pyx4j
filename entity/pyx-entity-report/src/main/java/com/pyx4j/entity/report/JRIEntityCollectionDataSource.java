/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Mar 23, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.report;

import java.util.Collection;
import java.util.Iterator;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

import org.apache.commons.beanutils.PropertyUtils;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;

public class JRIEntityCollectionDataSource<E extends IEntity> implements JRRewindableDataSource {

    protected static final PropertyNameProvider FIELD_NAME_PROPERTY_NAME_PROVIDER = new PropertyNameProvider() {
        @Override
        public String getPropertyName(JRField field) {
            return field.getName();
        }
    };

    protected static final PropertyNameProvider FIELD_DESCRIPTION_PROPERTY_NAME_PROVIDER = new PropertyNameProvider() {
        @Override
        public String getPropertyName(JRField field) {
            if (field.getDescription() == null) {
                return field.getName();
            }
            return field.getDescription();
        }
    };

    private final Collection<E> data;

    private Iterator<E> iterator;

    private E currentEntity;

    protected PropertyNameProvider propertyNameProvider = FIELD_NAME_PROPERTY_NAME_PROVIDER;

    public JRIEntityCollectionDataSource(Collection<E> beanCollection) {

        this.data = beanCollection;

        if (this.data != null) {
            this.iterator = this.data.iterator();
        }
    }

    @Override
    public boolean next() {
        boolean hasNext = false;

        if (this.iterator != null) {
            hasNext = this.iterator.hasNext();

            if (hasNext) {
                this.currentEntity = this.iterator.next();
            }
        }

        return hasNext;
    }

    @Override
    public Object getFieldValue(JRField field) throws JRException {
        return getFieldValue(currentEntity, field);
    }

    @Override
    public void moveFirst() {
        if (this.data != null) {
            this.iterator = this.data.iterator();
        }
    }

    /**
     * Returns the underlying bean collection used by this data source.
     * 
     * @return the underlying bean collection
     */
    public Collection<E> getData() {
        return data;
    }

    /**
     * Returns the total number of records/beans that this data source contains.
     * 
     * @return the total number of records of this data source
     */
    public int getRecordCount() {
        return data == null ? 0 : data.size();
    }

    /**
     * Clones this data source by creating a new instance that reuses the same underlying
     * bean collection.
     * 
     * @return a clone of this data source
     */
    public JRIEntityCollectionDataSource<E> cloneDataSource() {
        return new JRIEntityCollectionDataSource<E>(data);
    }

    /**
     *
     */
    interface PropertyNameProvider {
        public String getPropertyName(JRField field);
    }

    protected Object getFieldValue(E entity, JRField field) throws JRException {
        return getEntityProperty(entity, getPropertyName(field));
    }

    protected static Object getEntityProperty(IEntity entity, String propertyName) throws JRException {
        Object value = null;

        if (entity != null) {

            value = entity.getValue(new Path(entity.getObjectClass().getSimpleName() + "/" + propertyName + "/"));

        }

        return value;
    }

    protected String getPropertyName(JRField field) {
        return propertyNameProvider.getPropertyName(field);
    }
}