/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-28
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.tenant.insurance.GeneralInsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.GeneralInsurancePolicy;
import com.propertyvista.portal.rpc.portal.services.resident.TenantInsuranceByOtherProviderManagementService;
import com.propertyvista.portal.server.security.TenantAppContext;

public class TenantInsuranceByOtherProviderManagementServiceImpl implements TenantInsuranceByOtherProviderManagementService {

    @Override
    public void save(AsyncCallback<VoidSerializable> callback, GeneralInsuranceCertificate insuranceDetails) {
        if (insuranceDetails.insurancePolicy().isNull()) {
            GeneralInsurancePolicy policy = EntityFactory.create(GeneralInsurancePolicy.class);
            policy.tenant().set(TenantAppContext.getCurrentUserTenant());
            policy.certificate().set(insuranceDetails);
            policy.certificate().isManagedByTenant().setValue(true);
            policy.isDeleted().setValue(false);
            Persistence.secureSave(policy);
        }
        Persistence.secureSave(insuranceDetails); // security checks must be performed via data access rule
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void get(AsyncCallback<GeneralInsuranceCertificate> callback) {
        EntityQueryCriteria<GeneralInsurancePolicy> criteria = EntityQueryCriteria.create(GeneralInsurancePolicy.class);
        criteria.eq(criteria.proto().tenant(), TenantAppContext.getCurrentUserTenant());
        criteria.ge(criteria.proto().certificate().expiryDate(), new LogicalDate(SystemDateManager.getDate()));

        GeneralInsurancePolicy insurancePolicy = Persistence.secureRetrieve(criteria);
        GeneralInsuranceCertificate insuranceCertificate = insurancePolicy.certificate();
        if (insuranceCertificate == null) {
            insuranceCertificate = EntityFactory.create(GeneralInsuranceCertificate.class);
        }
        callback.onSuccess(insuranceCertificate);
    }
}
