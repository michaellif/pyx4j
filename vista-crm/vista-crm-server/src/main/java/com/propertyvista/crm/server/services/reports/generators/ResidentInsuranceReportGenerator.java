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
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

import com.propertyvista.biz.tenant.insurance.TenantInsuranceFacade;
import com.propertyvista.crm.rpc.dto.reports.ResidentInsuranceStatusDTO;
import com.propertyvista.domain.reports.ResidentInsuranceReportMetadata;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.NoInsuranceTenantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.OtherProviderTenantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantInsuranceStatusDTO;

public class ResidentInsuranceReportGenerator implements ReportGenerator {

    @Override
    public ReportProgressStatus getProgressStatus() {
        return null;
    }

    @Override
    public Serializable generateReport(ReportMetadata reportMetadata) {
        ResidentInsuranceReportMetadata residentInsuranceReportMetadata = (ResidentInsuranceReportMetadata) reportMetadata;

        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.eq(criteria.proto().status(), Lease.Status.Active);
        ICursorIterator<Lease> leaseIterator = Persistence.secureQuery(null, criteria, AttachLevel.Attached);
        Vector<ResidentInsuranceStatusDTO> reportData = new Vector<ResidentInsuranceStatusDTO>();
        try {
            while (leaseIterator.hasNext()) {
                Lease lease = leaseIterator.next();
                Tenant tenant = null;
                for (LeaseTermTenant t : lease.currentTerm().version().tenants()) {
                    if (t.takeOwnership().isBooleanTrue()) {
                        tenant = t.leaseParticipant();
                        break;
                    }
                }

                TenantInsuranceStatusDTO insuranceStatus = ServerSideFactory.create(TenantInsuranceFacade.class).getInsuranceStatus(tenant);

                ResidentInsuranceStatusDTO status = EntityFactory.create(ResidentInsuranceStatusDTO.class);
                if (!(insuranceStatus instanceof NoInsuranceTenantInsuranceStatusDTO)) {
                    status.hasResidentInsurance().setValue(false);
                    status.liabilityCoverage().setValue(status.liabilityCoverage().getValue());
                    status.startDate().setValue(status.startDate().getValue());
                    status.expiryDate().setValue(status.expiryDate().getValue());

                    status.provider().setValue(status.provider().getValue());
                    if ((insuranceStatus instanceof OtherProviderTenantInsuranceStatusDTO)) {
                        status.certificateFile(); // TODO add url for the link file
                    }
                } else {
                    status.hasResidentInsurance().setValue(false);
                }

                status.namesOnLease().setValue(tenant.customer().person().name().getStringView()); // TODO add the rest of people
                status.building().setValue(lease.unit().building().propertyCode().getValue());
                status.address().setValue(lease.unit().building().info().address().getStringView());
                status.postalCode().setValue(lease.unit().building().info().address().postalCode().getValue());
                status.unit().setValue(lease.unit().info().number().getValue());

                reportData.add(status);
            }

        } finally {
            IOUtils.closeQuietly(leaseIterator);
        }

        return reportData;
    }

    @Override
    public void abort() {
        // TODO Auto-generated method stub
    }

}
