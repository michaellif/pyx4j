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
package com.propertyvista.interfaces.importer;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.upload.FileUploadRegistry;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.tenant.insurance.GeneralInsuranceFacade;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.tenant.insurance.GeneralInsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificateScan;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.interfaces.importer.converter.InsuranceCertificateConverter;
import com.propertyvista.interfaces.importer.model.InsuranceCertificateIO;
import com.propertyvista.interfaces.importer.model.InsuranceCertificateScanIO;

public class ImportInsuranceCertificateDataProcessor {

    public void importModel(ImportProcessorContext context, Lease lease, Tenant tenant, InsuranceCertificateIO certificateIO) {
        if (certificateIO.propertyVistaIntegrated().getValue(false)) {
            EntityQueryCriteria<TenantSureInsurancePolicy> criteria = EntityQueryCriteria.create(TenantSureInsurancePolicy.class);
            criteria.eq(criteria.proto().certificate().insuranceCertificateNumber(), certificateIO.insuranceCertificateNumber());
            TenantSureInsurancePolicy policyOriginal = Persistence.service().retrieve(criteria);
            if (policyOriginal == null) {
                context.monitor.addErredEvent("Insurance", "Lease " + lease.leaseId().getStringView() + " TenantSure policy not found");
                return;
            }

            // Just change the owner of Insurance
            InsurancePaymentMethod paymentMethod = ServerSideFactory.create(PaymentMethodFacade.class).retrieveInsurancePaymentMethod(policyOriginal.tenant());
            paymentMethod.tenant().set(tenant);
            Persistence.service().persist(paymentMethod);

            policyOriginal.tenant().set(tenant);
            Persistence.service().persist(policyOriginal);

            policyOriginal.client().tenant().set(tenant);
            Persistence.service().persist(policyOriginal.client());

        } else {
            GeneralInsuranceCertificate certificate = EntityFactory.create(GeneralInsuranceCertificate.class);
            new InsuranceCertificateConverter().copyTOtoBO(certificateIO, certificate);

            for (InsuranceCertificateScanIO scanIO : certificateIO.certificateScans()) {

                InsuranceCertificateScan scan = EntityFactory.create(InsuranceCertificateScan.class);
                scan.description().setValue(scanIO.description().getValue());
                // Copy BLOB from DB
                InsuranceCertificateScan scanOriginal = Persistence.service().retrieve(InsuranceCertificateScan.class, new Key(scanIO.uri().getValue()));
                if (scanOriginal == null) {
                    context.monitor.addErredEvent("Insurance", "Lease " + lease.leaseId().getStringView() + " Insurance scan not found");
                    return;
                }

                scan.file().set(scanOriginal.file());
                FileUploadRegistry.register(scanOriginal.file());

                certificate.certificateDocs().add(scan);
            }

            ServerSideFactory.create(GeneralInsuranceFacade.class).createGeneralTenantInsurance(tenant, certificate);
        }
    }
}
