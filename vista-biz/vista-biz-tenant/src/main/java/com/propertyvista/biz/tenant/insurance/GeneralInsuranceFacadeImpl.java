/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 27, 2013
 * @author Artyom
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.tenant.insurance.GeneralInsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.GeneralInsurancePolicy;
import com.propertyvista.domain.tenant.lease.Tenant;

public class GeneralInsuranceFacadeImpl implements GeneralInsuranceFacade {

    @Override
    public void createGeneralTenantInsurance(Tenant tenantId, GeneralInsuranceCertificate certificate) {
        assertNewCertificate(certificate);
        GeneralInsurancePolicy policy = EntityFactory.create(GeneralInsurancePolicy.class);
        policy.tenant().set(tenantId);
        policy.certificate().set(certificate);
        policy.isDeleted().setValue(false);
        Persistence.secureSave(policy);
    }

    @Override
    public void deleteGeneralInsurance(GeneralInsuranceCertificate deletedCertificateId) {
        EntityQueryCriteria<GeneralInsurancePolicy> criteria = EntityQueryCriteria.create(GeneralInsurancePolicy.class);
        criteria.eq(criteria.proto().certificate(), deletedCertificateId);
        GeneralInsurancePolicy policy = Persistence.service().retrieve(criteria);
        if (policy == null) {
            throw new IllegalArgumentException("insurance policy that owns certificate id " + deletedCertificateId + " was not found");
        }
        policy.isDeleted().setValue(true);
        Persistence.service().merge(policy);
    }

    private void assertNewCertificate(GeneralInsuranceCertificate certificate) {
        if (certificate.getPrimaryKey() != null) {
            throw new IllegalArgumentException("this certificate is not new: id=" + certificate.getPrimaryKey());
        }
    }

}
