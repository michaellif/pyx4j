/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 16, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.converter;

import com.pyx4j.entity.shared.utils.EntityBinder;

import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.interfaces.importer.model.InsuranceCertificateIO;

@SuppressWarnings("rawtypes")
public class InsuranceCertificateConverter extends EntityBinder<InsuranceCertificate, InsuranceCertificateIO> {

    public InsuranceCertificateConverter() {
        super(InsuranceCertificate.class, InsuranceCertificateIO.class, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void bind() {
        bind(toProto.managedByTenant(), boProto.isManagedByTenant());
        bind(toProto.insuranceProvider(), boProto.insuranceProvider());
        bind(toProto.insuranceCertificateNumber(), boProto.insuranceCertificateNumber());
        bind(toProto.liabilityCoverage(), boProto.liabilityCoverage());
        bind(toProto.inceptionDate(), boProto.inceptionDate());
        bind(toProto.expiryDate(), boProto.expiryDate());
    }

}
