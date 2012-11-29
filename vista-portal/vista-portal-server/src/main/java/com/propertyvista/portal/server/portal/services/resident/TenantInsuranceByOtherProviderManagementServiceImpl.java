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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.portal.rpc.portal.services.resident.TenantInsuranceByOtherProviderManagementService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class TenantInsuranceByOtherProviderManagementServiceImpl implements TenantInsuranceByOtherProviderManagementService {

    @Override
    public void save(AsyncCallback<VoidSerializable> callback, InsuranceCertificate insuranceDetails) {
        if (insuranceDetails.tenant().isNull()) {
            insuranceDetails.tenant().set(TenantAppContext.getCurrentUserTenant());
        }
        Persistence.secureSave(insuranceDetails); // security checks must be performed via data access rule
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void get(AsyncCallback<InsuranceCertificate> callback) {
        EntityQueryCriteria<InsuranceCertificate> criteria = EntityQueryCriteria.create(InsuranceCertificate.class);
        criteria.eq(criteria.proto().tenant(), TenantAppContext.getCurrentUserTenant());
        criteria.ge(criteria.proto().expiryDate(), new LogicalDate(Persistence.service().getTransactionSystemTime()));

        InsuranceCertificate insuranceCertificate = Persistence.secureRetrieve(criteria);
        if (insuranceCertificate == null) {
            insuranceCertificate = EntityFactory.create(InsuranceCertificate.class);
            insuranceCertificate.documents().add(insuranceCertificate.documents().$());
        }
        callback.onSuccess(insuranceCertificate);
    }

}
