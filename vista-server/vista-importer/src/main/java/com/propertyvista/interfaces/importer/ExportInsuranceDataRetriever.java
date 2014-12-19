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
 */
package com.propertyvista.interfaces.importer;

import java.util.ArrayList;
import java.util.Collection;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.tenant.insurance.TenantInsuranceFacade;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificateScan;
import com.propertyvista.domain.tenant.insurance.PropertyVistaIntegratedInsurance;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.interfaces.importer.converter.InsuranceCertificateConverter;
import com.propertyvista.interfaces.importer.model.InsuranceCertificateIO;
import com.propertyvista.interfaces.importer.model.InsuranceCertificateScanIO;

public class ExportInsuranceDataRetriever {

    public Collection<InsuranceCertificateIO> getModel(Tenant tenant) {
        Collection<InsuranceCertificateIO> certificates = new ArrayList<>();

        for (InsuranceCertificate<?> certificate : ServerSideFactory.create(TenantInsuranceFacade.class).getInsuranceCertificates(tenant, true)) {
            InsuranceCertificateIO certificateIO = new InsuranceCertificateConverter().createTO(certificate);
            certificateIO.propertyVistaIntegrated().setValue(certificate.cast() instanceof PropertyVistaIntegratedInsurance);

            for (InsuranceCertificateScan scan : certificate.certificateDocs()) {
                InsuranceCertificateScanIO scanIO = EntityFactory.create(InsuranceCertificateScanIO.class);
                scanIO.description().setValue(scan.description().getValue());

                //TODO add file to generated zip
                scanIO.uri().setValue(scan.id().getStringView());

                certificateIO.certificateScans().add(scanIO);
            }
            certificates.add(certificateIO);
        }

        return certificates;
    }
}
