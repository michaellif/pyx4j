/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 16, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.oapi.xml.Action;
import com.propertyvista.oapi.xml.ElementIO;
import com.propertyvista.oapi.xml.ListIO;
import com.propertyvista.oapi.xml.PrimitiveIO;

public abstract class AbstractMarshaller<ValueType extends IEntity, BoundType> {

    public abstract BoundType marshal(ValueType v);

    public abstract ValueType unmarshal(BoundType v);

    public ListIO<BoundType> marshal(Collection<ValueType> collection) {
        ListIO<BoundType> ioList = new ListIO<BoundType>();
        for (ValueType item : collection) {
            ioList.getValue().add(marshal(item));
        }
        return ioList;
    }

    public List<ValueType> unmarshal(ListIO<BoundType> listIO) {
        List<ValueType> list = new ArrayList<ValueType>();
        for (BoundType ioItem : listIO.getValue()) {
            list.add(unmarshal(ioItem));
        }
        return list;
    }

    /**
     * 
     * Unmarshals elementIO->entity
     * 
     */
    public <T extends IEntity, E extends ElementIO> void set(T entity, E elementIO, AbstractMarshaller<T, E> marshaller) {
        if (elementIO != null) {

            if (elementIO.getAction() == Action.delete) {
                entity.setValue(null);
            } else {
                try {
                    entity.set(marshaller.unmarshal(elementIO));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            entity = null;
        }
    }

    /**
     * 
     * sets value for IPrimitive if primitiveIO is not null
     * 
     */
    public <T extends Serializable> void setValue(IPrimitive<T> primitive, PrimitiveIO<T> primitiveIO) {
        if (primitiveIO != null) {
            primitive.setValue(primitiveIO.getValue());
        }
    }

    /**
     * 
     * returns PrimitiveIO if neither IPrimitive nor its value is null
     * 
     */
    public <T extends Serializable, E extends PrimitiveIO<T>> E createIo(Class<E> classIO, IPrimitive<T> primitive) {
        if (primitive != null && !primitive.isNull()) {
            return createIo(classIO, primitive.getValue());
        }
        return null;
    }

    public <T extends Serializable, E extends PrimitiveIO<T>> E createIo(Class<E> classIO, T value) {
        if (value != null) {
            E primitiveIO;
            try {
                primitiveIO = classIO.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            primitiveIO.setValue(value);
            return primitiveIO;
        }
        return null;
    }

    /**
     * 
     * returns IPrimitive's value if neither IPrimitive nor its value is null
     * 
     */
    public <T extends Serializable> T getValue(IPrimitive<T> primitive) {
        if (primitive != null && !primitive.isNull()) {
            T result = primitive.getValue();
            return result;
        }
        return null;
    }
}