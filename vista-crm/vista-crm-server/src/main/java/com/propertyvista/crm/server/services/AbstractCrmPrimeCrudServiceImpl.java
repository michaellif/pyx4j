/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2015
 * @author vlads
 */
package com.propertyvista.crm.server.services;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.shared.utils.EntityBinder;
import com.pyx4j.essentials.server.docs.sheet.AbstractDownloadableCrudServiceDtoImpl;

import com.propertyvista.crm.rpc.services.AbstractCrmPrimeCrudService;

public abstract class AbstractCrmPrimeCrudServiceImpl<BO extends IEntity, TO extends IEntity> extends AbstractDownloadableCrudServiceDtoImpl<BO, TO> implements
        AbstractCrmPrimeCrudService<TO> {

    protected AbstractCrmPrimeCrudServiceImpl(Class<BO> entityClass, Class<TO> dtoClass) {
        super(entityClass, dtoClass);
    }

    protected AbstractCrmPrimeCrudServiceImpl(EntityBinder<BO, TO> binder) {
        super(binder);
    }

}
