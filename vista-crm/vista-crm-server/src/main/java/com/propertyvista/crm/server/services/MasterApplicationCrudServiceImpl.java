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
package com.propertyvista.crm.server.services;

import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.crm.rpc.services.MasterApplicationCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.TenantInLease.Role;
import com.propertyvista.domain.tenant.ptapp.MasterApplication;
import com.propertyvista.domain.tenant.ptapp.MasterApplication.Decision;
import com.propertyvista.dto.MasterApplicationDTO;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.server.common.charges.PriceCalculationHelpers;
import com.propertyvista.server.common.util.TenantConverter;
import com.propertyvista.server.common.util.TenantInLeaseRetriever;

public class MasterApplicationCrudServiceImpl extends GenericCrudServiceDtoImpl<MasterApplication, MasterApplicationDTO> implements
        MasterApplicationCrudService {

    public MasterApplicationCrudServiceImpl() {
        super(MasterApplication.class, MasterApplicationDTO.class);
    }

    @Override
    protected void enhanceDTO(MasterApplication in, MasterApplicationDTO dto, boolean fromList) {
        super.enhanceDTO(in, dto, fromList);

        Persistence.service().retrieve(dto.lease());
        Persistence.service().retrieve(dto.lease().unit());
        Persistence.service().retrieve(dto.lease().unit().belongsTo());

        TenantInLeaseRetriever.UpdateLeaseTenants(dto.lease());
        dto.numberOfOccupants().setValue(dto.lease().tenants().size());
        dto.numberOfCoApplicants().setValue(0);
        dto.numberOfGuarantors().setValue(0);

        for (TenantInLease tenantInLease : dto.lease().tenants()) {
            Persistence.service().retrieve(tenantInLease);

            if (tenantInLease.role().getValue() == Role.Applicant) {
                dto.mainApplicant().set(tenantInLease.tenant());
            } else if (tenantInLease.role().getValue() == Role.CoApplicant) {
                dto.numberOfCoApplicants().setValue(dto.numberOfCoApplicants().getValue() + 1);
            }

            if (!fromList) {
                TenantInLeaseRetriever tr = new TenantInLeaseRetriever(tenantInLease.getPrimaryKey(), true);
                dto.tenantsWithInfo().add(createTenantInfoDTO(tr));
                dto.tenantFinancials().add(createTenantFinancialDTO(tr));
            }
        }

        // calculate price adjustments:
        PriceCalculationHelpers.calculateChargeItemAdjustments(dto.lease().serviceAgreement().serviceItem());

        dto.rentPrice().setValue(dto.lease().serviceAgreement().serviceItem().adjustedPrice().getValue());
        dto.parkingPrice().setValue(0.);
        dto.otherPrice().setValue(0.);
        dto.deposit().setValue(0.);

        for (ChargeItem item : dto.lease().serviceAgreement().featureItems()) {
            PriceCalculationHelpers.calculateChargeItemAdjustments(item); // calculate price adjustments
            if (item.item().product() instanceof Feature) {
                switch (((Feature) item.item().product()).type().getValue()) {
                case parking:
                    dto.parkingPrice().setValue(dto.parkingPrice().getValue() + item.adjustedPrice().getValue());
                    break;

                default:
                    dto.otherPrice().setValue(dto.otherPrice().getValue() + item.adjustedPrice().getValue());
                }
            }
        }

        dto.discounts().setValue(!dto.lease().serviceAgreement().concessions().isEmpty());

        // TODO: currently - just some smockup stuff:
        dto.deposit().setValue(100 + RandomUtil.randomDouble(1000));
        dto.percenrtageApproved().setValue(RandomUtil.randomDouble(100));
        dto.suggestedDecision().setValue(RandomUtil.randomEnum(Decision.class));

        if (dto.suggestedDecision().getValue() != Decision.Pending) {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            dto.decidedBy().set(RandomUtil.random(Persistence.service().query(criteria)));

            dto.decisionDate().setValue(RandomUtil.randomLogicalDate(2011, 2012));
            dto.decisionReason().setValue("Decided according current application state and Equifax check results");
        }
    }

    // internal helpers:
    private TenantInfoDTO createTenantInfoDTO(TenantInLeaseRetriever tr) {
        TenantInfoDTO tiDTO = new TenantConverter.Tenant2TenantInfo().createDTO(tr.tenant);
        new TenantConverter.TenantScreening2TenantInfo().copyDBOtoDTO(tr.tenantScreening, tiDTO);
        return tiDTO;
    }

    private TenantFinancialDTO createTenantFinancialDTO(TenantInLeaseRetriever tr) {
        TenantFinancialDTO tfDTO = new TenantConverter.TenantFinancialEditorConverter().createDTO(tr.tenantScreening);
        tfDTO.person().set(tr.tenant.person());
        return tfDTO;
    }
}
