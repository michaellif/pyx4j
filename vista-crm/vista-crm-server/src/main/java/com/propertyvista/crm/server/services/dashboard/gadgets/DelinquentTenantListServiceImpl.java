/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 20, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.crm.rpc.dto.gadgets.DelinquentTenantDTO;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.TransactionHistoryDTO;

public class DelinquentTenantListServiceImpl extends AbstractCrudServiceDtoImpl<Tenant, DelinquentTenantDTO> {

    public DelinquentTenantListServiceImpl() {
        super(Tenant.class, DelinquentTenantDTO.class);

    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void enhanceListRetrieved(Tenant leaseCustomerTenant, DelinquentTenantDTO dto) {
        super.enhanceListRetrieved(leaseCustomerTenant, dto);

        Persistence.service().retrieveMember(dto.lease());

        TransactionHistoryDTO transactionsHistory = ServerSideFactory.create(ARFacade.class).getTransactionHistory(dto.lease().billingAccount());
        dto.arrears().set(transactionsHistory.totalAgingBuckets().detach());
    }
}
