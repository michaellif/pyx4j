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
import java.util.Comparator;
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
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.ExtantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.InsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.NoInsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.OtherProviderInsuranceStatusDTO;

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

        insuranceCertificate = getBestInsuranceCertificate(Persistence.service().query(ownInsuranceCriteira));
        if (insuranceCertificate == null) {
            EntityQueryCriteria<InsuranceCertificate> anyNonExpiredInsuranceCriteria = EntityQueryCriteria.create(InsuranceCertificate.class);
            anyNonExpiredInsuranceCriteria.eq(anyNonExpiredInsuranceCriteria.proto().tenant().lease().leaseParticipants(), tenantId);
            anyNonExpiredInsuranceCriteria.eq(anyNonExpiredInsuranceCriteria.proto().isDeleted(), Boolean.FALSE);
            anyNonExpiredInsuranceCriteria.or(PropertyCriterion.gt(anyNonExpiredInsuranceCriteria.proto().expiryDate(), today),
                    PropertyCriterion.isNull(anyNonExpiredInsuranceCriteria.proto().expiryDate()));
            insuranceCertificate = getBestInsuranceCertificate(Persistence.service().query(anyNonExpiredInsuranceCriteria));
        }

        return insuranceCertificate;
    }

    @Override
    public InsuranceStatusDTO getInsuranceStatus(Tenant tenantId) {

        InsuranceCertificate insuranceCertificate = getInsuranceCertificate(tenantId);

        if (insuranceCertificate == null) {
            NoInsuranceStatusDTO noInsuranceStatus = EntityFactory.create(NoInsuranceStatusDTO.class);

            TenantInsurancePolicy tenantInsurancePolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(retrieveLease(tenantId).unit(),
                    TenantInsurancePolicy.class);
            noInsuranceStatus.minimumRequiredLiability().setValue(tenantInsurancePolicy.minimumRequiredLiability().getValue());
            noInsuranceStatus.noInsuranceStatusMessage().setValue(tenantInsurancePolicy.noInsuranceStatusMessage().getValue());
            noInsuranceStatus.tenantInsuranceInvitation().setValue(tenantInsurancePolicy.tenantInsuranceInvitation().getValue());

            return noInsuranceStatus;
        } else {
            ExtantInsuranceStatusDTO insuranceStatus = null;
            if (insuranceCertificate.isPropertyVistaIntegratedProvider().isBooleanTrue()) {
                // TODO currently TenantSure is the only integrated provider so we don't try to understand which one it is
                insuranceStatus = ServerSideFactory.create(TenantSureFacade.class).getStatus(tenantId);
            } else {

                OtherProviderInsuranceStatusDTO otherProviderStatus = EntityFactory.create(OtherProviderInsuranceStatusDTO.class);
                insuranceStatus = otherProviderStatus;
            }
            insuranceStatus.insuranceProvider().setValue(insuranceCertificate.insuranceProvider().getValue());
            insuranceStatus.isOwner().setValue(insuranceCertificate.tenant().getPrimaryKey().equals(tenantId.getPrimaryKey()));

            insuranceStatus.liabilityCoverage().setValue(insuranceCertificate.liabilityCoverage().getValue());
            insuranceStatus.expiryDate().setValue(insuranceCertificate.expiryDate().getValue());
            return insuranceStatus;
        }
    }

    private static Lease retrieveLease(Tenant tenantId) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        Tenant tenant = Persistence.service().retrieve(Tenant.class, tenantId.getPrimaryKey());
        Persistence.service().retrieve(Lease.class, tenant.lease().getPrimaryKey());
        return Persistence.service().retrieve(criteria);
    }

    /** this one chooses the best insurance certificate out of all insurance certificates */
    private InsuranceCertificate getBestInsuranceCertificate(List<InsuranceCertificate> insuranceCertificates) {
        if (insuranceCertificates.isEmpty()) {
            return null;
        }
        ArrayList<InsuranceCertificate> sortedInsuranceCertificates = new ArrayList<InsuranceCertificate>(insuranceCertificates);
        java.util.Collections.sort(sortedInsuranceCertificates, new Comparator<InsuranceCertificate>() {

            @Override
            public int compare(InsuranceCertificate o1, InsuranceCertificate o2) {
                if ((o1.getInstanceValueClass().equals(InsuranceTenantSure.class)) && !(o2.getInstanceValueClass().equals(InsuranceTenantSure.class))) {
                    return -1;
                } else if (!(o1.getInstanceValueClass().equals(InsuranceTenantSure.class)) && (o2.getInstanceValueClass().equals(InsuranceTenantSure.class))) {
                    return 1;
                } else {
                    return -o1.liabilityCoverage().getValue().compareTo(o2.liabilityCoverage().getValue());
                }
            }
        });
        return sortedInsuranceCertificates.get(0);
    }
}
