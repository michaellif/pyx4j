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

    private final boolean inCollection;

    MarshallingContext(Class<? extends IEntity> element, boolean inCollection, MarshallingContext parent) {
        this.parent = parent;
        this.element = element;
        this.inCollection = inCollection;
    }

    public MarshallingContext getParent() {
        return parent;
    }

    public Class<? extends IEntity> getElement() {
        return element;
    }

    public boolean isCollectionContext() {
        return inCollection;
    }

    public boolean hasParentOf(Class<? extends IEntity> element) {
        return findParentContext(element) != null;
    }

    public boolean hasParentCollectionOf(Class<? extends IEntity> element) {
        MarshallingContext result = findParentContext(element);
        return result == null ? false : result.isCollectionContext();
    }

    private MarshallingContext findParentContext(Class<? extends IEntity> element) {
        MarshallingContext result = this;
        while ((result = result.getParent()) != null) {
            if (result.getElement().equals(element)) {
                return result;
            }
        }
        return null;
    }
}
