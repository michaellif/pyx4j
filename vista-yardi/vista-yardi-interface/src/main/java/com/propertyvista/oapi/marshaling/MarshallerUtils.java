/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 6, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.marshaling;

import java.io.Serializable;
import java.util.Collection;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.oapi.xml.Action;
import com.propertyvista.oapi.xml.ElementIO;
import com.propertyvista.oapi.xml.PrimitiveIO;

public class MarshallerUtils {

    /**
     * 
     * Marshals elementIO->entity
     * 
     */
    public static <T extends IEntity, E extends ElementIO> void set(T entity, E elementIO, Marshaller<T, E> marshaller) {
        if (elementIO != null) {

            if (elementIO.getAction() == Action.nil) {
                entity.setValue(null);
            } else {
                try {
                    entity.set(marshaller.unmarshal(elementIO));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 
     * Marshals elementIO->entity and adds entity to entity list.
     * 
     */

    public static <T extends IEntity, E extends ElementIO> void add(Collection<T> entities, T entity, E elementIO, Marshaller<T, E> marshaller) {
        set(entity, elementIO, marshaller);
        if (entity != null) {
            entities.add(entity);
        }
    }

    public static <T extends Serializable> void setValue(IPrimitive<T> primitive, PrimitiveIO<T> primitiveIO) {
        if (primitiveIO != null) {
            primitive.setValue(primitiveIO.getValue());
        }
    }

    public static <T extends Serializable, E extends PrimitiveIO<T>> E createIo(Class<E> classIO, IPrimitive<T> primitive) {
        if (primitive != null && !primitive.isNull()) {
            E primitiveIO;
            try {
                primitiveIO = classIO.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            primitiveIO.setValue(primitive.getValue());
            return primitiveIO;
        }
        return null;
    }
}
