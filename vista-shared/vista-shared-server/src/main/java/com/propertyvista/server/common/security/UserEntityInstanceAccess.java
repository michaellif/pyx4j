/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-15
 * @author vlads
 */
package com.propertyvista.server.common.security;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.security.InstanceAccess;

import com.propertyvista.biz.system.VistaContext;
import com.propertyvista.domain.IUserEntity;

public class UserEntityInstanceAccess implements InstanceAccess {

    private static final long serialVersionUID = -3642752850612488708L;

    @Override
    public boolean implies(IEntity entity) {
        return (entity instanceof IUserEntity) && (EqualsHelper.equals(((IUserEntity) entity).user().getPrimaryKey(), VistaContext.getCurrentUserPrimaryKey()));
    }

}
