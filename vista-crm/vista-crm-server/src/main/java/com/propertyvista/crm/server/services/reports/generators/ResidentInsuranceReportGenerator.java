/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.generators;

import java.io.Serializable;
import java.util.Vector;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.services.reports.ReportGenerator;
import com.pyx4j.essentials.server.services.reports.ReportProgressStatus;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

import com.propertyvista.biz.tenant.insurance.TenantInsuranceFacade;
import com.propertyvista.crm.rpc.dto.reports.ResidentInsuranceStatusDTO;
import com.propertyvista.domain.reports.ResidentInsuranceReportMetadata;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;

public class ResidentInsuranceReportGenerator implements ReportGenerator {

    private static final I18n i18n = I18n.get(ResidentInsuranceReportGenerator.class);

    private volatile boolean isAborted = false;

    private volatile ReportProgressStatus progressStatus;

    @Override
    public ReportProgressStatus getProgressStatus() {
        return progressStatus;
    }

    @Override
    public Serializable generateReport(ReportMetadata reportMetadata) {
        ResidentInsuranceReportMetadata residentInsuranceReportMetadata = (ResidentInsuranceReportMetadata) reportMetadata;

        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.eq(criteria.proto().status(), Lease.Status.Active);
        criteria.asc(criteria.proto().unit().building().propertyCode());
        criteria.asc(criteria.proto().unit().info().number());

        int totalStatuses = Persistence.service().count(criteria);
        int currentStatus = 0;

        ICursorIterator<Lease> leaseIterator = Persistence.secureQuery(null, criteria, AttachLevel.Attached);
        Vector<ResidentInsuranceStatusDTO> reportData = new Vector<ResidentInsuranceStatusDTO>();
        try {
            while (leaseIterator.hasNext() & !isAborted) {
                progressStatus = new ReportProgressStatus(i18n.tr("Retrieving Data"), 1, 1, ++currentStatus, totalStatuses);
                Lease lease = leaseIterator.next();
                Tenant tenant = null;
                Persistence.service().retrieveMember(lease.currentTerm().version().tenants());
                for (LeaseTermTenant t : lease.currentTerm().version().tenants()) {
                    if (t.role().getValue() == Role.Applicant) {
                        tenant = t.leaseParticipant();
                        break;
                    }
                }

                ResidentInsuranceStatusDTO status = EntityFactory.create(ResidentInsuranceStatusDTO.class);
                status.namesOnLease_().set(tenant.<Tenant> createIdentityStub());
                InsuranceCertificate insuranceCertificate = ServerSideFactory.create(TenantInsuranceFacade.class).getInsuranceCertificate(tenant);

                if (insuranceCertificate != null) {
                    status.hasResidentInsurance().setValue(true);
                    status.liabilityCoverage().setValue(insuranceCertificate.liabilityCoverage().getValue());

                    status.startDate().setValue(insuranceCertificate.inceptionDate().getValue());
                    status.expiryDate().setValue(insuranceCertificate.expiryDate().getValue());
                    status.provider().setValue(insuranceCertificate.insuranceProvider().getValue());
                    status.certificate().setValue(insuranceCertificate.insuranceCertificateNumber().getValue());
                    status.certificate_().set(tenant.createIdentityStub());
                    // TODO add url for the link file
                } else {
                    status.hasResidentInsurance().setValue(false);
                }

                status.namesOnLease().setValue(tenant.customer().person().name().getStringView()); // TODO add the rest of people
                Persistence.service().retrieve(lease.unit().building());
                status.building().setValue(lease.unit().building().propertyCode().getValue());
                status.address().setValue(lease.unit().building().info().address().getStringView());
                status.postalCode().setValue(lease.unit().building().info().address().postalCode().getValue());
                status.unit().setValue(lease.unit().info().number().getValue());

                if (residentInsuranceReportMetadata.onlyLeasesWithInsurance().isBooleanTrue() && !status.hasResidentInsurance().isBooleanTrue()) {
                    continue;
                }
                reportData.add(status);
            }

        } finally {
            IOUtils.closeQuietly(leaseIterator);
        }

        return reportData;
    }

    @Override
    public void abort() {
        isAborted = true;
    }

}
