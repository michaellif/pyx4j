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

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.policy.policies.TenantInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure.CancellationType;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure.Status;
import com.propertyvista.domain.tenant.insurance.TenantSureConstants;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.NoInsuranceTenantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.OtherProviderTenantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantSureTenantInsuranceStatusShortDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureMessageDTO;

public class TenantInsuranceFacadeImpl implements TenantInsuranceFacade {

    private static final I18n i18n = I18n.get(TenantInsuranceFacadeImpl.class);

    @Override
    public TenantInsuranceStatusDTO getInsuranceStatus(Tenant tenantId) {
        LogicalDate today = new LogicalDate(Persistence.service().getTransactionSystemTime());
        EntityQueryCriteria<InsuranceCertificate> criteria = EntityQueryCriteria.create(InsuranceCertificate.class);
        criteria.eq(criteria.proto().tenant().lease().leaseParticipants(), tenantId);
        criteria.or(PropertyCriterion.ge(criteria.proto().expirationDate(), today), PropertyCriterion.isNull(criteria.proto().expirationDate()));
        InsuranceCertificate insuranceCertificate = Persistence.service().retrieve(criteria);

        if (insuranceCertificate == null) {
            NoInsuranceTenantInsuranceStatusDTO noInsuranceStatus = EntityFactory.create(NoInsuranceTenantInsuranceStatusDTO.class);

            TenantInsurancePolicy tenantInsurancePolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(retrieveLease(tenantId).unit(),
                    TenantInsurancePolicy.class);
            noInsuranceStatus.minimumRequiredLiability().setValue(tenantInsurancePolicy.minimumRequiredLiability().getValue());
            noInsuranceStatus.noInsuranceStatusMessage().setValue(tenantInsurancePolicy.noInsuranceStatusMessage().getValue());
            noInsuranceStatus.tenantInsuranceInvitation().setValue(tenantInsurancePolicy.tenantInsuranceInvitation().getValue());
            return noInsuranceStatus;
        } else {
            TenantInsuranceStatusDTO insuranceStatus = null;
            if (TenantSureConstants.TENANTSURE_LEGAL_NAME.equals(insuranceCertificate.insuranceProvider().getValue())) {

                EntityQueryCriteria<InsuranceTenantSure> tsCriteria = EntityQueryCriteria.create(InsuranceTenantSure.class);
                criteria.eq(tsCriteria.proto().insuranceCertificate(), insuranceCertificate);
                InsuranceTenantSure insuranceTenantSure = Persistence.service().retrieve(tsCriteria);

                TenantSureTenantInsuranceStatusShortDTO tenantSureStatus = EntityFactory.create(TenantSureTenantInsuranceStatusShortDTO.class);
                tenantSureStatus.monthlyPremiumPayment().setValue(insuranceTenantSure.monthlyPayable().getValue());

                GregorianCalendar cal = new GregorianCalendar();
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.MONTH));
                cal.add(Calendar.DAY_OF_MONTH, 1);
                tenantSureStatus.nextPaymentDate().setValue(new LogicalDate(cal.getTime()));

                if (insuranceTenantSure.status().getValue() == Status.PendingCancellation) {
                    TenantSureMessageDTO message = tenantSureStatus.messages().$();
                    if (insuranceTenantSure.cancellation().getValue() == CancellationType.SkipPayment) {
                        message.messageText()
                                .setValue(
                                        i18n.tr("There was a problem with your last scheduled payment. If you don't update your credit card details until {0,date,short}, your TeantSure insurance will expire on {1,date,short}.",
                                                insuranceTenantSure.expiryDate().getValue()));
                    } else {
                        message.messageText().setValue(
                                i18n.tr("Your insurance has been cancelled and will expire on {0,date,short}", insuranceTenantSure.expiryDate().getValue()));
                    }
                }

                insuranceStatus = tenantSureStatus;
            } else {
                OtherProviderTenantInsuranceStatusDTO otherProviderStatus = EntityFactory.create(OtherProviderTenantInsuranceStatusDTO.class);
                insuranceStatus = otherProviderStatus;
            }

            insuranceStatus.isOwner().setValue(insuranceCertificate.tenant().getPrimaryKey().equals(tenantId.getPrimaryKey()));

            insuranceStatus.liabilityCoverage().setValue(insuranceCertificate.personalLiability().getValue());
            insuranceStatus.expirationDate().setValue(insuranceCertificate.expirationDate().getValue());

            return insuranceStatus;
        }

    }

    private static Lease retrieveLease(Tenant tenantId) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        Tenant tenant = Persistence.service().retrieve(Tenant.class, tenantId.getPrimaryKey());
        Persistence.service().retrieve(Lease.class, tenant.lease().getPrimaryKey());
        return Persistence.service().retrieve(criteria);
    }
}
