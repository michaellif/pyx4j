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

import com.pyx4j.entity.core.IVersionData;
import com.pyx4j.entity.rpc.AbstractVersionDataListService;
import com.pyx4j.entity.server.AbstractListServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.security.common.AbstractUser;

public abstract class AbstractVistaVersionDataListServiceImpl<BO extends IVersionData<?>, TO extends IVersionData<?>> extends
        AbstractListServiceDtoImpl<BO, TO> implements AbstractVersionDataListService<TO> {

    protected Class<? extends AbstractUser> userClass;

    public AbstractVistaVersionDataListServiceImpl(Class<BO> boClass, Class<TO> toClass, Class<? extends AbstractUser> userClass) {
        super(boClass, toClass);
        this.userClass = userClass;
    }

    @Override
    protected void enhanceListRetrieved(BO bo, TO to) {
        if (!bo.createdByUserKey().isNull()) {
            AbstractUser user = Persistence.service().retrieve(userClass, bo.createdByUserKey().getValue());
            if (user != null) {
                to.createdByUser().setValue(user.getStringView());
            }
        }
    }
}
