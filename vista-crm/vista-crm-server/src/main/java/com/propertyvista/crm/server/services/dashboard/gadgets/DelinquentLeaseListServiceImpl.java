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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.crm.rpc.dto.gadgets.DelinquentLeaseDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.DelinquentLeaseListService;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.TransactionHistoryDTO;

public class DelinquentLeaseListServiceImpl extends AbstractCrudServiceDtoImpl<Lease, DelinquentLeaseDTO> implements DelinquentLeaseListService {

    public DelinquentLeaseListServiceImpl() {
        super(Lease.class, DelinquentLeaseDTO.class);
    }

    @Override
    protected void bind() {
        bind(dtoProto.leasePrimaryKey(), dboProto.id());
        bind(dtoProto.leaseId(), dboProto.leaseId());
        bind(dtoProto.buildingPropertyCode(), dboProto.unit().building().propertyCode());
        bind(dtoProto.unitNumber(), dboProto.unit().info().number());
        bind(dtoProto.participantId(), dboProto.currentTerm().version().tenants().$().leaseParticipant().participantId());
        bind(dtoProto.primaryApplicantsFirstName(), dboProto.currentTerm().version().tenants().$().leaseParticipant().customer().person().name().firstName());
        bind(dtoProto.primaryApplicantsLastName(), dboProto.currentTerm().version().tenants().$().leaseParticipant().customer().person().name().lastName());
        bind(dtoProto.email(), dboProto.currentTerm().version().tenants().$().leaseParticipant().customer().person().email());
        bind(dtoProto.mobilePhone(), dboProto.currentTerm().version().tenants().$().leaseParticipant().customer().person().mobilePhone());
        bind(dtoProto.homePhone(), dboProto.currentTerm().version().tenants().$().leaseParticipant().customer().person().homePhone());
        bind(dtoProto.workPhone(), dboProto.currentTerm().version().tenants().$().leaseParticipant().customer().person().workPhone());
        bind(dtoProto.arrears(), dboProto.billingAccount().arrearsSnapshots().$().totalAgingBuckets());
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<Lease> dbCriteria, EntityListCriteria<DelinquentLeaseDTO> dtoCriteria) {
        dbCriteria.ne(dbCriteria.proto().status(), Lease.Status.Application);
        dbCriteria.ne(dbCriteria.proto().status(), Lease.Status.Closed);

        dbCriteria.eq(dbCriteria.proto().currentTerm().version().tenants().$().role(), Role.Applicant);

        if (dtoCriteria.getFilters() != null) {
            java.util.Iterator<Criterion> i = dtoCriteria.getFilters().iterator();

            while (i.hasNext()) {
                Criterion criterion = i.next();
                if (criterion instanceof PropertyCriterion) {
                    PropertyCriterion propertyCriterion = (PropertyCriterion) criterion;

                    if (propertyCriterion.getPropertyPath().equals(dtoProto.asOf().getPath().toString())) {
                        dbCriteria.le(dbCriteria.proto().billingAccount().arrearsSnapshots().$().fromDate(), propertyCriterion.getValue());
                        dbCriteria.ge(dbCriteria.proto().billingAccount().arrearsSnapshots().$().toDate(), propertyCriterion.getValue());
                        i.remove();
                    } else if (propertyCriterion.getPropertyPath().equals(dtoProto.building().getPath().toString())) {
                        dbCriteria.in(dbCriteria.proto().unit().building(), (Vector<Building>) propertyCriterion.getValue());
                        i.remove();
                    }
                }
            }
        }
        super.enhanceListCriteria(dbCriteria, dtoCriteria);

    }

    @Override
    protected void enhanceListRetrieved(Lease dbo, DelinquentLeaseDTO dto) {
        super.enhanceListRetrieved(dbo, dto);
        TransactionHistoryDTO transactionsHistory = ServerSideFactory.create(ARFacade.class).getTransactionHistory(dbo.billingAccount());

        Persistence.service().retrieveMember(dbo.currentTerm().version().tenants());
        Persistence.service().retrieve(dbo.unit().building());
        dto.buildingPropertyCode().setValue(dbo.unit().building().propertyCode().getValue());

        for (LeaseTermTenant tenant : dbo.currentTerm().version().tenants()) {
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

        dto.arrears().set(transactionsHistory.totalAgingBuckets().detach());
    }
}
