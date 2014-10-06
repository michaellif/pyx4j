/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 6, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.oapi;

import com.pyx4j.entity.core.IEntity;

public class MarshallingContext {

    private final MarshallingContext parent;

    private final Class<? extends IEntity> element;

    MarshallingContext(Class<? extends IEntity> element, MarshallingContext parent) {
        this.parent = parent;
        this.element = element; // null indicates collection marshalling
    }

    public MarshallingContext getParent() {
        return parent;
    }

    public Class<? extends IEntity> getElement() {
        return element;
    }

    public boolean isInCollection() {
        if (element == null) {
            // called on collection marshaller
            return true;
        } else {
            return parent == null ? false : parent.isInCollection(); // called on item marshaller
        }
    }
}
