/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 24, 2013
 * @author yuriyl
 * @version $Id$
 */
package com.propertyvista.yardi.merger;

import java.util.List;

import com.yardi.entity.mits.YardiCustomer;

import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

public class TenantMerger {

    boolean isNew = false;

    public boolean validateChanges(List<YardiCustomer> yardiCustomers, List<LeaseTermTenant> tenants) {
        // TODO Auto-generated method stub
        return false;
    }

    public LeaseTerm updateTenants(List<YardiCustomer> yardiCustomers, IList<LeaseTermTenant> tenants) {
        // TODO Auto-generated method stub
        return null;
    }

}
