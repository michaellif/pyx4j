/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 16, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.rpc.security;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.security.ForTypesAccessRule;

import com.propertyvista.dto.LeaseParticipantScreeningTO;

public class FormerTenantScreeningInstanceAccess extends ForTypesAccessRule {

    private static final long serialVersionUID = 1L;

    public FormerTenantScreeningInstanceAccess() {
        super(LeaseParticipantScreeningTO.class);
    }

    @Override
    public boolean implies(IEntity contextEntity) {
        return super.implies(contextEntity) && ((LeaseParticipantScreeningTO) contextEntity).leaseStatus().getValue().isFormer();
    }
}
