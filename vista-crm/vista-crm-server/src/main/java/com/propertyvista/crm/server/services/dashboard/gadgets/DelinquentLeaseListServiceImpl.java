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
import com.propertyvista.domain.financial.billing.LeaseAgingBuckets;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

public class DelinquentLeaseListServiceImpl extends AbstractCrudServiceDtoImpl<LeaseAgingBuckets, DelinquentLeaseDTO> implements DelinquentLeaseListService {

    public DelinquentLeaseListServiceImpl() {
        super(LeaseAgingBuckets.class, DelinquentLeaseDTO.class);
    }

    public EntityListCriteria<LeaseAgingBuckets> convertCriteria(EntityListCriteria<DelinquentLeaseDTO> dtoCriteria) {
        EntityListCriteria<LeaseAgingBuckets> dboCriteria = EntityListCriteria.create(LeaseAgingBuckets.class);
        enhanceListCriteria(dboCriteria, dtoCriteria);
        Persistence.applyDatasetAccessRule(dboCriteria);

        return dboCriteria;
    }

    @Override
    protected void bind() {
        bind(dtoProto.leasePrimaryKey(), dboProto.arrearsSnapshot().billingAccount().lease().id());
        bind(dtoProto.leaseId(), dboProto.arrearsSnapshot().billingAccount().lease().leaseId());
        bind(dtoProto.buildingPropertyCode(), dboProto.arrearsSnapshot().billingAccount().lease().unit().building().propertyCode());
        bind(dtoProto.unitNumber(), dboProto.arrearsSnapshot().billingAccount().lease().unit().info().number());

        bind(dtoProto.participantId(), dboProto.arrearsSnapshot().billingAccount().lease()._applicant().participantId());
        bind(dtoProto.primaryApplicantsFirstName(), dboProto.arrearsSnapshot().billingAccount().lease()._applicant().customer().person().name().firstName());
        bind(dtoProto.primaryApplicantsLastName(), dboProto.arrearsSnapshot().billingAccount().lease()._applicant().customer().person().name().lastName());
        bind(dtoProto.email(), dboProto.arrearsSnapshot().billingAccount().lease()._applicant().customer().person().email());
        bind(dtoProto.mobilePhone(), dboProto.arrearsSnapshot().billingAccount().lease()._applicant().customer().person().mobilePhone());
        bind(dtoProto.homePhone(), dboProto.arrearsSnapshot().billingAccount().lease()._applicant().customer().person().homePhone());
        bind(dtoProto.workPhone(), dboProto.arrearsSnapshot().billingAccount().lease()._applicant().customer().person().workPhone());

        bind(dtoProto.arrears().arCode(), dboProto.arCode());
        bind(dtoProto.arrears().bucketThisMonth(), dboProto.bucketThisMonth());
        bind(dtoProto.arrears().bucket30(), dboProto.bucket30());
        bind(dtoProto.arrears().bucket60(), dboProto.bucket60());
        bind(dtoProto.arrears().bucket90(), dboProto.bucket90());
        bind(dtoProto.arrears().bucketOver90(), dboProto.bucketOver90());
        bind(dtoProto.arrears().arrearsAmount(), dboProto.arrearsAmount());
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<LeaseAgingBuckets> dbCriteria, EntityListCriteria<DelinquentLeaseDTO> dtoCriteria) {
        dbCriteria.in(dbCriteria.proto().arrearsSnapshot().billingAccount().lease().status(), Lease.Status.active());

        if (dtoCriteria.getFilters() != null) {
            java.util.Iterator<Criterion> i = dtoCriteria.getFilters().iterator();

            while (i.hasNext()) {
                Criterion criterion = i.next();
                if (criterion instanceof PropertyCriterion) {
                    PropertyCriterion propertyCriterion = (PropertyCriterion) criterion;

                    if (propertyCriterion.getPropertyPath().equals(dtoProto.asOf().getPath().toString())) {
                        dbCriteria.le(dbCriteria.proto().arrearsSnapshot().fromDate(), propertyCriterion.getValue());
                        dbCriteria.ge(dbCriteria.proto().arrearsSnapshot().toDate(), propertyCriterion.getValue());
                        i.remove();
                    } else if (propertyCriterion.getPropertyPath().equals(dtoProto.building().getPath().toString())) {
                        dbCriteria.in(dbCriteria.proto().arrearsSnapshot().billingAccount().lease().unit().building(),
                                (Vector<Building>) propertyCriterion.getValue());
                        i.remove();
                    }
                }
            }
        }
        super.enhanceListCriteria(dbCriteria, dtoCriteria);

    }

    @Override
    protected void enhanceListRetrieved(LeaseAgingBuckets dbo, DelinquentLeaseDTO dto) {
        super.enhanceListRetrieved(dbo, dto);

        Persistence.service().retrieve(dbo.arrearsSnapshot());
        Persistence.service().retrieve(dbo.arrearsSnapshot().billingAccount());
        Persistence.service().retrieve(dbo.arrearsSnapshot().billingAccount().lease());

        dto.leasePrimaryKey().setValue(dbo.arrearsSnapshot().billingAccount().lease().id().getValue());
        dto.leaseId().setValue(dbo.arrearsSnapshot().billingAccount().lease().leaseId().getValue());

        dto.unitNumber().setValue(dbo.arrearsSnapshot().billingAccount().lease().unit().info().number().getValue());

        Persistence.service().retrieve(dbo.arrearsSnapshot().billingAccount().lease().unit().building());
        dto.buildingPropertyCode().setValue(dbo.arrearsSnapshot().billingAccount().lease().unit().building().propertyCode().getValue());

        Persistence.service().retrieve(dbo.arrearsSnapshot().billingAccount().lease()._applicant());
        dto.participantId().setValue(dbo.arrearsSnapshot().billingAccount().lease()._applicant().participantId().getValue());
        dto.primaryApplicantsFirstName()
                .setValue(dbo.arrearsSnapshot().billingAccount().lease()._applicant().customer().person().name().firstName().getValue());
        dto.primaryApplicantsLastName().setValue(dbo.arrearsSnapshot().billingAccount().lease()._applicant().customer().person().name().lastName().getValue());
        dto.email().setValue(dbo.arrearsSnapshot().billingAccount().lease()._applicant().customer().person().email().getValue());
        dto.mobilePhone().setValue(dbo.arrearsSnapshot().billingAccount().lease()._applicant().customer().person().mobilePhone().getValue());
        dto.homePhone().setValue(dbo.arrearsSnapshot().billingAccount().lease()._applicant().customer().person().homePhone().getValue());
        dto.workPhone().setValue(dbo.arrearsSnapshot().billingAccount().lease()._applicant().customer().person().workPhone().getValue());
    }
}
