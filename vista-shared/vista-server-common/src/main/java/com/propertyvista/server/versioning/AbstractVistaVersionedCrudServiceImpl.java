/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 29, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.versioning;

import com.pyx4j.entity.server.AbstractVersionedCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IVersionedEntity;

import com.propertyvista.domain.security.common.AbstractUser;

public abstract class AbstractVistaVersionedCrudServiceImpl<E extends IVersionedEntity<?>> extends AbstractVersionedCrudServiceImpl<E> {

    protected Class<? extends AbstractUser> userClass;

    public AbstractVistaVersionedCrudServiceImpl(Class<E> entityClass, Class<? extends AbstractUser> userClass) {
        super(entityClass);
        this.userClass = userClass;
    }

    @Override
    protected void enhanceRetrieved(E entity, E dto, RetrieveTarget retrieveTarget ) {
        setCreatedByUser(dto);
    }

    @Override
    protected void enhanceListRetrieved(E entity, E dto) {
        setCreatedByUser(dto);
    }

    protected void setCreatedByUser(E dto) {
        if (!dto.version().createdByUserKey().isNull()) {
            AbstractUser user = Persistence.service().retrieve(userClass, dto.version().createdByUserKey().getValue());
            if (user != null) {
                dto.version().createdByUser().setValue(user.getStringView());
            }
        }
    }

}
