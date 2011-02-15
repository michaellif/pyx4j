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
 * @version $Id$
 */
package com.propertyvista.portal.server.access;

import com.propertyvista.portal.domain.pt.IApplicationEntity;
import com.propertyvista.portal.server.pt.PtUserDataAccess;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.security.InstanceAccess;
import com.pyx4j.entity.shared.IEntity;

public class ApplicationEntityInstanceAccess implements InstanceAccess {

    private static final long serialVersionUID = -3642752850612488708L;

    @Override
    public boolean allow(IEntity entity) {
        return (entity instanceof IApplicationEntity)
                && (EqualsHelper.equals(((IApplicationEntity) entity).application().getPrimaryKey(), PtUserDataAccess.getCurrentUserApplication()));
    }

}
