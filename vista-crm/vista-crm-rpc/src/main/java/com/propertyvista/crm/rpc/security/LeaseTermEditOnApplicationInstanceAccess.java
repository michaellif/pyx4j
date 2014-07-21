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
import com.pyx4j.entity.security.InstanceAccess;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseTermDTO;

public class LeaseTermEditOnApplicationInstanceAccess implements InstanceAccess {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean implies(IEntity contextEntity) {
        return Lease.Status.Application == ((LeaseTermDTO) contextEntity).lease().status().getValue();
    }
}
