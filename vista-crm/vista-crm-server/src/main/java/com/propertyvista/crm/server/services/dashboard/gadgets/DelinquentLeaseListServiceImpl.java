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

import java.util.Vector;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.dto.gadgets.DelinquentLeaseDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.DelinquentLeaseListService;
import com.propertyvista.domain.financial.billing.LeaseArrearsSnapshot;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

public class DelinquentLeaseListServiceImpl extends AbstractCrudServiceDtoImpl<LeaseArrearsSnapshot, DelinquentLeaseDTO> implements DelinquentLeaseListService {

    public DelinquentLeaseListServiceImpl() {
        super(LeaseArrearsSnapshot.class, DelinquentLeaseDTO.class);
    }

    public EntityListCriteria<LeaseArrearsSnapshot> convertCriteria(EntityListCriteria<DelinquentLeaseDTO> dtoCriteria) {
        EntityListCriteria<LeaseArrearsSnapshot> dboCriteria = EntityListCriteria.create(LeaseArrearsSnapshot.class);
        enhanceListCriteria(dboCriteria, dtoCriteria);

        return dboCriteria;
    }

    @Override
    protected void bind() {
        bind(dtoProto.leasePrimaryKey(), dboProto.billingAccount().lease().id());
        bind(dtoProto.leaseId(), dboProto.billingAccount().lease().leaseId());
        bind(dtoProto.buildingPropertyCode(), dboProto.billingAccount().lease().unit().building().propertyCode());
        bind(dtoProto.unitNumber(), dboProto.billingAccount().lease().unit().info().number());
        bind(dtoProto.participantId(), dboProto.billingAccount().lease().currentTerm().version().tenants().$().leaseParticipant().participantId());
        bind(dtoProto.primaryApplicantsFirstName(), dboProto.billingAccount().lease().currentTerm().version().tenants().$().leaseParticipant().customer()
                .person().name().firstName());
        bind(dtoProto.primaryApplicantsLastName(), dboProto.billingAccount().lease().currentTerm().version().tenants().$().leaseParticipant().customer()
                .person().name().lastName());
        bind(dtoProto.email(), dboProto.billingAccount().lease().currentTerm().version().tenants().$().leaseParticipant().customer().person().email());
        bind(dtoProto.mobilePhone(), dboProto.billingAccount().lease().currentTerm().version().tenants().$().leaseParticipant().customer().person()
                .mobilePhone());
        bind(dtoProto.homePhone(), dboProto.billingAccount().lease().currentTerm().version().tenants().$().leaseParticipant().customer().person().homePhone());
        bind(dtoProto.workPhone(), dboProto.billingAccount().lease().currentTerm().version().tenants().$().leaseParticipant().customer().person().workPhone());
        bind(dtoProto.arrears(), dboProto.totalAgingBuckets());
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<LeaseArrearsSnapshot> dbCriteria, EntityListCriteria<DelinquentLeaseDTO> dtoCriteria) {
        dbCriteria.ne(dbCriteria.proto().billingAccount().lease().status(), Lease.Status.Application);
        dbCriteria.ne(dbCriteria.proto().billingAccount().lease().status(), Lease.Status.Closed);

        dbCriteria.eq(dbCriteria.proto().billingAccount().lease().currentTerm().version().tenants().$().role(), Role.Applicant);

        if (dtoCriteria.getFilters() != null) {
            java.util.Iterator<Criterion> i = dtoCriteria.getFilters().iterator();

            while (i.hasNext()) {
                Criterion criterion = i.next();
                if (criterion instanceof PropertyCriterion) {
                    PropertyCriterion propertyCriterion = (PropertyCriterion) criterion;

                    if (propertyCriterion.getPropertyPath().equals(dtoProto.asOf().getPath().toString())) {
                        dbCriteria.le(dbCriteria.proto().fromDate(), propertyCriterion.getValue());
                        dbCriteria.ge(dbCriteria.proto().toDate(), propertyCriterion.getValue());
                        i.remove();
                    } else if (propertyCriterion.getPropertyPath().equals(dtoProto.building().getPath().toString())) {
                        dbCriteria.in(dbCriteria.proto().billingAccount().lease().unit().building(), (Vector<Building>) propertyCriterion.getValue());
                        i.remove();
                    }
                }
            }
        }
        super.enhanceListCriteria(dbCriteria, dtoCriteria);

    }

    @Override
    protected void enhanceListRetrieved(LeaseArrearsSnapshot dbo, DelinquentLeaseDTO dto) {
        super.enhanceListRetrieved(dbo, dto);

        Persistence.service().retrieve(dbo.billingAccount());
        Persistence.service().retrieve(dbo.billingAccount().lease());

        dto.leasePrimaryKey().setValue(dbo.billingAccount().lease().id().getValue());
        dto.leaseId().setValue(dbo.billingAccount().lease().leaseId().getValue());

        dto.unitNumber().setValue(dbo.billingAccount().lease().unit().info().number().getValue());

        Persistence.service().retrieve(dbo.billingAccount().lease().unit().building());

        dto.buildingPropertyCode().setValue(dbo.billingAccount().lease().unit().building().propertyCode().getValue());

        Persistence.service().retrieveMember(dbo.billingAccount().lease().currentTerm().version().tenants());

        for (LeaseTermTenant tenant : dbo.billingAccount().lease().currentTerm().version().tenants()) {
            if (tenant.role().getValue() == Role.Applicant) {
                dto.participantId().setValue(tenant.leaseParticipant().participantId().getValue());
                dto.primaryApplicantsFirstName().setValue(tenant.leaseParticipant().customer().person().name().firstName().getValue());
                dto.primaryApplicantsLastName().setValue(tenant.leaseParticipant().customer().person().name().lastName().getValue());
                dto.mobilePhone().setValue(tenant.leaseParticipant().customer().person().mobilePhone().getValue());
                dto.workPhone().setValue(tenant.leaseParticipant().customer().person().workPhone().getValue());
                dto.homePhone().setValue(tenant.leaseParticipant().customer().person().homePhone().getValue());
                dto.email().setValue(tenant.leaseParticipant().customer().person().email().getValue());
                break;
            }
        }

    }
}
