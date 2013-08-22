/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.policy.policies.TenantInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.NoInsuranceTenantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.OtherProviderTenantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantSureTenantInsuranceStatusShortDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureTenantInsuranceStatusDetailedDTO;

public class TenantInsuranceFacadeImpl implements TenantInsuranceFacade {

    private static final I18n i18n = I18n.get(TenantInsuranceFacadeImpl.class);

    @Override
    public InsuranceCertificate getInsuranceCertificate(Tenant tenantId) {
        LogicalDate today = new LogicalDate(SystemDateManager.getDate());

        // try to get current insurance certificate either tenant's own or the insurance certificate of the room mate
        InsuranceCertificate insuranceCertificate = null;
        EntityQueryCriteria<InsuranceCertificate> ownInsuranceCriteira = EntityQueryCriteria.create(InsuranceCertificate.class);
        ownInsuranceCriteira.eq(ownInsuranceCriteira.proto().tenant(), tenantId);
        ownInsuranceCriteira.eq(ownInsuranceCriteira.proto().isDeleted(), Boolean.FALSE);
        ownInsuranceCriteira.or(PropertyCriterion.gt(ownInsuranceCriteira.proto().expiryDate(), today),
                PropertyCriterion.isNull(ownInsuranceCriteira.proto().expiryDate()));
        insuranceCertificate = Persistence.service().retrieve(ownInsuranceCriteira);
        if (insuranceCertificate == null) {
            EntityQueryCriteria<InsuranceCertificate> anyNonExpiredInsuranceCriteria = EntityQueryCriteria.create(InsuranceCertificate.class);
            anyNonExpiredInsuranceCriteria.eq(anyNonExpiredInsuranceCriteria.proto().tenant().lease().leaseParticipants(), tenantId);
            anyNonExpiredInsuranceCriteria.eq(anyNonExpiredInsuranceCriteria.proto().isDeleted(), Boolean.FALSE);
            anyNonExpiredInsuranceCriteria.or(PropertyCriterion.gt(anyNonExpiredInsuranceCriteria.proto().expiryDate(), today),
                    PropertyCriterion.isNull(anyNonExpiredInsuranceCriteria.proto().expiryDate()));
            insuranceCertificate = Persistence.service().retrieve(anyNonExpiredInsuranceCriteria);
        }

        return insuranceCertificate;
    }

    @Override
    public TenantInsuranceStatusDTO getInsuranceStatus(Tenant tenantId) {

        InsuranceCertificate insuranceCertificate = getInsuranceCertificate(tenantId);

        TenantInsuranceStatusDTO insuranceStatus = null;
        if (insuranceCertificate == null) {
            NoInsuranceTenantInsuranceStatusDTO noInsuranceStatus = EntityFactory.create(NoInsuranceTenantInsuranceStatusDTO.class);

            TenantInsurancePolicy tenantInsurancePolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(retrieveLease(tenantId).unit(),
                    TenantInsurancePolicy.class);
            noInsuranceStatus.minimumRequiredLiability().setValue(tenantInsurancePolicy.minimumRequiredLiability().getValue());
            noInsuranceStatus.noInsuranceStatusMessage().setValue(tenantInsurancePolicy.noInsuranceStatusMessage().getValue());
            noInsuranceStatus.tenantInsuranceInvitation().setValue(tenantInsurancePolicy.tenantInsuranceInvitation().getValue());
            insuranceStatus = noInsuranceStatus;
        } else {
            if (insuranceCertificate.isPropertyVistaIntegratedProvider().isBooleanTrue()) {
                // TODO currently TenantSure is the only integrated provider so we don't try to understand which one it is
                TenantSureTenantInsuranceStatusDetailedDTO tsStatusDetailed = ServerSideFactory.create(TenantSureFacade.class).getStatus(tenantId);

                TenantSureTenantInsuranceStatusShortDTO tsStatusShort = EntityFactory.create(TenantSureTenantInsuranceStatusShortDTO.class);
                tsStatusShort.monthlyPremiumPayment().setValue(tsStatusDetailed.nextPaymentDetails().total().getValue());
                tsStatusShort.messages().addAll(tsStatusDetailed.messages());
                tsStatusShort.nextPaymentDate().setValue(tsStatusDetailed.nextPaymentDetails().paymentDate().getValue());

                insuranceStatus = tsStatusShort;
            } else {

                OtherProviderTenantInsuranceStatusDTO otherProviderStatus = EntityFactory.create(OtherProviderTenantInsuranceStatusDTO.class);
                insuranceStatus = otherProviderStatus;
            }

            insuranceStatus.isOwner().setValue(insuranceCertificate.tenant().getPrimaryKey().equals(tenantId.getPrimaryKey()));

            insuranceStatus.liabilityCoverage().setValue(insuranceCertificate.liabilityCoverage().getValue());
            insuranceStatus.expirationDate().setValue(insuranceCertificate.expiryDate().getValue());

        }

        return insuranceStatus;

    }

    private static Lease retrieveLease(Tenant tenantId) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        Tenant tenant = Persistence.service().retrieve(Tenant.class, tenantId.getPrimaryKey());
        Persistence.service().retrieve(Lease.class, tenant.lease().getPrimaryKey());
        return Persistence.service().retrieve(criteria);
    }
}
