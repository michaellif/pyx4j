/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease.common;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;

import com.propertyvista.domain.tenant.Guarantor2;
import com.propertyvista.domain.tenant.Tenant2;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease2;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.dto.LeaseDTO2;

public abstract class LeaseCrudServiceBase2Impl<DTO extends LeaseDTO2> extends AbstractCrudServiceDtoImpl<Lease2, DTO> {

    protected LeaseCrudServiceBase2Impl(Class<DTO> dtoClass) {
        super(Lease2.class, dtoClass);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(Lease2 in, DTO dto) {
        enhanceRetrievedCommon(in, dto);

        // load detached entities:
//        Persistence.service().retrieve(dto.billingAccount().adjustments());
//        Persistence.service().retrieve(dto.billingAccount().deposits());
//      Persistence.service().retrieve(dto.documents());

        loadDetachedProducts(dto);

        for (Tenant2 item : dto.currentLeaseTerm().version().tenants()) {
            Persistence.service().retrieve(item.screening(), AttachLevel.ToStringMembers);
        }

        for (Guarantor2 item : dto.currentLeaseTerm().version().guarantors()) {
            Persistence.service().retrieve(item.screening(), AttachLevel.ToStringMembers);
        }
    }

    @Override
    protected void enhanceListRetrieved(Lease2 in, DTO dto) {
        enhanceRetrievedCommon(in, dto);
    }

    private void enhanceRetrievedCommon(Lease2 in, DTO dto) {
        // load detached entities:
        Persistence.service().retrieve(dto.unit());
        Persistence.service().retrieve(dto.unit().building());

        if (dto.currentLeaseTerm().version().isNull()) {
            dto.currentLeaseTerm().set(Persistence.secureRetrieveDraft(LeaseTerm.class, dto.currentLeaseTerm().getPrimaryKey()));
        }

        Persistence.service().retrieve(dto.leaseTerms());
        if (!dto.leaseTerms().isEmpty()) {
            dto.leaseFrom().set(dto.leaseTerms().get(0).leaseFrom());
            dto.leaseTo().set(dto.leaseTerms().get(dto.leaseTerms().size() - 1).leaseTo());
        }

        Persistence.service().retrieve(dto.currentLeaseTerm().version().tenants());
        Persistence.service().retrieve(dto.currentLeaseTerm().version().guarantors());

//        Persistence.service().retrieve(dto.billingAccount());
    }

    @Override
    protected void persist(Lease2 dbo, DTO in) {
        throw new Error("Facade should be used");
    }

    protected void loadDetachedProducts(DTO dto) {
        Persistence.service().retrieve(dto.currentLeaseTerm().version().leaseProducts().serviceItem().item().product());

        for (BillableItem item : dto.currentLeaseTerm().version().leaseProducts().featureItems()) {
            Persistence.service().retrieve(item.item().product());
        }
    }
}