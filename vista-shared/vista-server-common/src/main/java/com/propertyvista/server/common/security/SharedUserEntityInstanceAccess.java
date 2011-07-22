/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.security;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.security.InstanceAccess;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.domain.ISharedUserEntity;

public class SharedUserEntityInstanceAccess implements InstanceAccess {

    private static final long serialVersionUID = -8216688423251918735L;

    @Override
    public boolean allow(IEntity entity) {

        return (entity instanceof ISharedUserEntity)
                && ((Key.DORMANT_KEY.equals(((ISharedUserEntity) entity).user().getPrimaryKey())) || EqualsHelper.equals(((ISharedUserEntity) entity).user()
                        .getPrimaryKey(), VistaContext.getCurrentUserPrimaryKey()));

    }
}
