/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 17, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.versioning;

import com.pyx4j.entity.rpc.AbstractVersionDataListService;
import com.pyx4j.entity.server.AbstractListServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IVersionData;

import com.propertyvista.domain.security.AbstractUser;

public abstract class AbstractVistaVersionDataListServiceImpl<E extends IVersionData<?>> extends AbstractListServiceImpl<E> implements
        AbstractVersionDataListService<E> {

    protected Class<? extends AbstractUser> userClass;

    public AbstractVistaVersionDataListServiceImpl(Class<E> entityClass, Class<? extends AbstractUser> userClass) {
        super(entityClass);
        this.userClass = userClass;
    }

    @Override
    protected void enhanceListRetrieved(E entity, E dto) {
        if (!entity.createdByUserKey().isNull()) {
            AbstractUser user = Persistence.service().retrieve(userClass, entity.createdByUserKey().getValue());
            if (user != null) {
                dto.createdByUser().setValue(user.getStringView());
            }
        }
    }
}
