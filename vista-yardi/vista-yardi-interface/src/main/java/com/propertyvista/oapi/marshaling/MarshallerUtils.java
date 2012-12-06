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

import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.oapi.xml.PrimitiveIO;

public class MarshallerUtils {

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
