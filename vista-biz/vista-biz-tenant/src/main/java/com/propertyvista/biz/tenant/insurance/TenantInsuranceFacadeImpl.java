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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import com.propertyvista.domain.tenant.insurance.PropertyVistaIntegratedInsurance;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.GeneralInsuranceCertificateSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.InsuranceCertificateSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.InsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.TenantSureCertificateSummaryDTO;

public class TenantInsuranceFacadeImpl implements TenantInsuranceFacade {

    private static final I18n i18n = I18n.get(TenantInsuranceFacadeImpl.class);

    @Override
    public List<InsuranceCertificate> getInsuranceCertificates(Tenant tenantId, boolean ownedOnly) {
        LogicalDate today = new LogicalDate(SystemDateManager.getDate());

        // try to get current insurance certificate either tenant's own or the insurance certificate of the room mate
        EntityQueryCriteria<InsuranceCertificate> ownInsuranceCriteira = EntityQueryCriteria.create(InsuranceCertificate.class);
        if (ownedOnly) {
            ownInsuranceCriteira.eq(ownInsuranceCriteira.proto().insurancePolicy().tenant(), tenantId);
        } else {
            ownInsuranceCriteira.eq(ownInsuranceCriteira.proto().insurancePolicy().tenant().lease().leaseParticipants(), tenantId);
        }
        ownInsuranceCriteira.eq(ownInsuranceCriteira.proto().insurancePolicy().isDeleted(), Boolean.FALSE);
        ownInsuranceCriteira.or(PropertyCriterion.gt(ownInsuranceCriteira.proto().expiryDate(), today),
                PropertyCriterion.isNull(ownInsuranceCriteira.proto().expiryDate()));

        List<InsuranceCertificate> certificates = Persistence.service().query(ownInsuranceCriteira);
        for (InsuranceCertificate certificate : certificates) {
            Persistence.service().retrieve(certificate.insurancePolicy());
            Persistence.service().retrieve(certificate.insurancePolicy().tenant());
        }
        List<InsuranceCertificate> sorted = sortInsuranceCertificates(certificates, tenantId);
        for (InsuranceCertificate certificate : sorted) {
            certificate.insurancePolicy().detach();
        }
        return sorted;
    }

    @Override
    public InsuranceStatusDTO getInsuranceStatus(Tenant tenantId) {

        InsuranceStatusDTO insuranceStatusDTO = EntityFactory.create(InsuranceStatusDTO.class);

        TenantInsurancePolicy tenantInsurancePolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(retrieveLease(tenantId).unit(),
                TenantInsurancePolicy.class);
        insuranceStatusDTO.minimumRequiredLiability().setValue(tenantInsurancePolicy.minimumRequiredLiability().getValue());

        for (InsuranceCertificate<?> certificate : getInsuranceCertificates(tenantId, false)) {
            InsuranceCertificateSummaryDTO certificateSummaryDTO = null;

            if (certificate instanceof PropertyVistaIntegratedInsurance) {
                // TODO currently TenantSure is the only integrated provider so we don't try to understand which one it is
                certificateSummaryDTO = ServerSideFactory.create(TenantSureFacade.class).getStatus(tenantId);
            } else {
                GeneralInsuranceCertificateSummaryDTO otherProviderStatus = EntityFactory.create(GeneralInsuranceCertificateSummaryDTO.class);
                certificateSummaryDTO = otherProviderStatus;
            }

            certificateSummaryDTO.setPrimaryKey(certificate.getPrimaryKey());
            certificateSummaryDTO.insuranceCertificateNumber().setValue(certificate.insuranceCertificateNumber().getValue());
            certificateSummaryDTO.insurancePolicy().set(certificate.insurancePolicy());

            certificateSummaryDTO.insuranceProvider().setValue(certificate.insuranceProvider().getValue());
            certificateSummaryDTO.isOwner().setValue(certificate.insurancePolicy().tenant().getPrimaryKey().equals(tenantId.getPrimaryKey()));

            certificateSummaryDTO.liabilityCoverage().setValue(certificate.liabilityCoverage().getValue());
            certificateSummaryDTO.inceptionDate().setValue(certificate.inceptionDate().getValue());
            certificateSummaryDTO.expiryDate().setValue(certificate.expiryDate().getValue());

            insuranceStatusDTO.certificates().add(certificateSummaryDTO);
        }

        if (insuranceStatusDTO.certificates().size() == 0) {
            insuranceStatusDTO.status().setValue(InsuranceStatusDTO.Status.noInsurance);
        } else {
            insuranceStatusDTO.status().setValue(InsuranceStatusDTO.Status.hasOtherInsurance);
            for (InsuranceCertificateSummaryDTO c : insuranceStatusDTO.certificates()) {
                if (c instanceof TenantSureCertificateSummaryDTO) {
                    insuranceStatusDTO.status().setValue(InsuranceStatusDTO.Status.hasTenantSure);
                    break;
                }
            }
        }

        boolean isExpiryDateDefined = true;
        LogicalDate coverageExpiryDate = null;
        for (InsuranceCertificateSummaryDTO c : insuranceStatusDTO.certificates()) {
            if (!c.expiryDate().isNull()) {
                if (coverageExpiryDate == null || c.expiryDate().getValue().compareTo(coverageExpiryDate) > 0) {
                    coverageExpiryDate = c.expiryDate().getValue();
                }
            } else {
                isExpiryDateDefined = false;
                break;
            }
        }
        if (isExpiryDateDefined) {
            insuranceStatusDTO.coverageExpiryDate().setValue(coverageExpiryDate);
        }

        return insuranceStatusDTO;
    }

    private static Lease retrieveLease(Tenant tenantId) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        Tenant tenant = Persistence.service().retrieve(Tenant.class, tenantId.getPrimaryKey());
        Persistence.service().retrieve(Lease.class, tenant.lease().getPrimaryKey());
        return Persistence.service().retrieve(criteria);
    }

    /** this one chooses the best insurance certificate out of all insurance certificates */
    private List<InsuranceCertificate> sortInsuranceCertificates(List<InsuranceCertificate> insuranceCertificates, final Tenant tenantId) {
        if (insuranceCertificates.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<InsuranceCertificate> sortedInsuranceCertificates = new ArrayList<InsuranceCertificate>(insuranceCertificates);
        java.util.Collections.sort(sortedInsuranceCertificates, new InsuranceCertificateComparator(tenantId));
        return sortedInsuranceCertificates;
    }
}
