/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-29
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.client.visor.notes;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IEntity;

public class NotesParentId {

    private final Key ownerId;

    private final String ownerClass;

    public NotesParentId(Class<? extends IEntity> entityClass, Key ownerId) {
        super();
        this.ownerId = ownerId.asCurrentKey();
        this.ownerClass = getEntitySimpleClassName(entityClass);
    }

    // TODO : this algorithm should be revised 
    //TODO use EntityMeta or ClassName.getClassName(klass)
    private final String getEntitySimpleClassName(Class<? extends IEntity> entityClass) {
        return GWTJava5Helper.getSimpleName(entityClass);
    }

    public Key getOwnerId() {
        return ownerId;
    }

    public String getOwnerClass() {
        return ownerClass;
    }
}
