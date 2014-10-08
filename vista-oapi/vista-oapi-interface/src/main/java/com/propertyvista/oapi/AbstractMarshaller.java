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

import com.propertyvista.oapi.xml.AbstractListIO;
import com.propertyvista.oapi.xml.ElementIO;
import com.propertyvista.oapi.xml.Note;
import com.propertyvista.oapi.xml.PrimitiveIO;

public abstract class AbstractMarshaller<ValueType extends IEntity, BoundType> {

    private static ThreadLocal<MarshallingContext> context = new ThreadLocal<MarshallingContext>();

    protected abstract BoundType marshal(ValueType v);

    protected abstract ValueType unmarshal(BoundType v);

    private void setContext(ValueType element, boolean inCollection) {
        context.set(new MarshallingContext(element == null ? null : element.getInstanceValueClass(), inCollection, context.get()));
    }

    private void restoreContext() {
        if (context.get() != null) {
            context.set(context.get().getParent());
        }
    }

    protected MarshallingContext getContext() {
        return context.get();
    }

    public final <C extends AbstractListIO<BoundType>> C marshalCollection(Class<C> collectionClass, Collection<ValueType> collection) {
        try {
            C ioList = collectionClass.newInstance();
            for (ValueType item : collection) {
                ioList.add(marshalItem(item, true));
            }
            return ioList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final BoundType marshalItem(ValueType item) {
        return this.marshalItem(item, false);
    }

    private BoundType marshalItem(ValueType item, boolean inCollection) {
        setContext(item, inCollection);
        try {
            return marshal(item);
        } finally {
            restoreContext();
        }
    }

    public final <C extends AbstractListIO<BoundType>> List<ValueType> unmarshalCollection(C listIO) {
        List<ValueType> list = new ArrayList<ValueType>();
        for (BoundType ioItem : listIO.getList()) {
            list.add(unmarshal(ioItem));
        }
        return list;
    }

    public final ValueType unmarshalItem(BoundType v) {
        return unmarshal(v);
    }

    /**
     * 
     * Unmarshals elementIO->entity
     * 
     */
    public <T extends IEntity, E extends ElementIO> void set(T entity, E elementIO, AbstractMarshaller<T, E> marshaller) {
        if (elementIO != null) {

            if (elementIO.getNote() == Note.actionDelete) {
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

    public <T extends Serializable, E extends PrimitiveIO<T>> E detachedIo(Class<E> classIO) {
        try {
            E primitiveIO = classIO.newInstance();
            primitiveIO.setNote(Note.contentDetached);
            return primitiveIO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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