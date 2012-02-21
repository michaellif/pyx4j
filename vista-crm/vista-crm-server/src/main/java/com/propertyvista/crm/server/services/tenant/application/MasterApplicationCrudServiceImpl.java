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
package com.propertyvista.crm.server.services.tenant.application;

import java.math.BigDecimal;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.dto.MasterApplicationActionDTO;
import com.propertyvista.crm.rpc.services.tenant.application.MasterApplicationCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.crm.server.util.GenericConverter;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.TenantInLease.Role;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.ptapp.MasterApplication;
import com.propertyvista.dto.MasterApplicationDTO;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.server.common.charges.PriceCalculationHelpers;
import com.propertyvista.server.common.ptapp.ApplicationManager;
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

            TenantInLeaseRetriever tr = new TenantInLeaseRetriever(tenantInLease.getPrimaryKey(), true);
            dto.tenantInfo().add(createTenantInfoDTO(tr));
            TenantFinancialDTO tf = createTenantFinancialDTO(tr);
            dto.numberOfGuarantors().setValue(dto.numberOfGuarantors().getValue() + tf.guarantors().size());
            dto.tenantFinancials().add(tf);
        }

        if (!fromList) {
            dto.masterApplicationStatus().set(ApplicationManager.calculateStatus(in));
        }

        calculatePrices(in, dto);

        // TODO: currently - just some mockup stuff:
        dto.deposit().setValue(new BigDecimal(100 + RandomUtil.randomDouble(1000)));
    }

    // internal helpers:
    private TenantInfoDTO createTenantInfoDTO(TenantInLeaseRetriever tr) {
        TenantInfoDTO tiDTO = new TenantConverter.Tenant2TenantInfo().createDTO(tr.getTenant());
        new TenantConverter.TenantScreening2TenantInfo().copyDBOtoDTO(tr.tenantScreening, tiDTO);
        return tiDTO;
    }

    private TenantFinancialDTO createTenantFinancialDTO(TenantInLeaseRetriever tr) {
        TenantFinancialDTO tfDTO = new TenantConverter.TenantFinancialEditorConverter().createDTO(tr.tenantScreening);
        tfDTO.person().set(tr.getPerson());
        return tfDTO;
    }

    private void calculatePrices(MasterApplication in, MasterApplicationDTO dto) {
        // calculate price adjustments:
        PriceCalculationHelpers.calculateChargeItemAdjustments(dto.lease().serviceAgreement().serviceItem());

        dto.rentPrice().setValue(dto.lease().serviceAgreement().serviceItem()._currentPrice().getValue());
        dto.parkingPrice().setValue(new BigDecimal(0));
        dto.otherPrice().setValue(new BigDecimal(0));
        dto.deposit().setValue(new BigDecimal(0));

        for (BillableItem item : dto.lease().serviceAgreement().featureItems()) {
            PriceCalculationHelpers.calculateChargeItemAdjustments(item); // calculate price adjustments
            if (item.item().product() instanceof Feature) {
                switch (((Feature) item.item().product()).type().getValue()) {
                case parking:
                    dto.parkingPrice().setValue(dto.parkingPrice().getValue().add(item._currentPrice().getValue()));
                    break;

                default:
                    dto.otherPrice().setValue(dto.otherPrice().getValue().add(item._currentPrice().getValue()));
                }
            }
        }

        dto.discounts().setValue(!dto.lease().serviceAgreement().concessions().isEmpty());
    }

    @Override
    public void action(AsyncCallback<MasterApplicationDTO> callback, MasterApplicationActionDTO actionDTO) {
        MasterApplication dbo = Persistence.service().retrieve(dboClass, actionDTO.getPrimaryKey());

        dbo.status().setValue(actionDTO.status().getValue());
        dbo.decidedBy().set(CrmAppContext.getCurrentUserEmployee());
        dbo.decisionReason().setValue(actionDTO.decisionReason().getValue());
        dbo.decisionDate().setValue(new LogicalDate());

        Persistence.service().merge(dbo);

        // Update lease status if necessary:
        Persistence.service().retrieve(dbo.lease());
        switch (actionDTO.status().getValue()) {
        case Approved:
            dbo.lease().status().setValue(Status.Approved);
            break;
        case Declined:
            dbo.lease().status().setValue(Status.Declined);
            break;
        case Cancelled:
            dbo.lease().status().setValue(Status.ApplicationCancelled);
            break;
        }

        Persistence.service().merge(dbo.lease());

        MasterApplicationDTO dto2 = GenericConverter.convertDBO2DTO(dbo, dtoClass);
        enhanceDTO(dbo, dto2, false);
        callback.onSuccess(dto2);
    }
}
