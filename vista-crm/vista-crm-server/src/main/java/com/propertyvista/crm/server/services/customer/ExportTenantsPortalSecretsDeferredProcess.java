/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-16
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer;

import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.report.ReportTableFormatter;
import com.pyx4j.essentials.server.report.ReportTableXLSXFormatter;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.TenantPortalAccessInformationDTO;

public class ExportTenantsPortalSecretsDeferredProcess extends AbstractDeferredProcess {

    private static final long serialVersionUID = 5026863365365592547L;

    private volatile int progress;

    private volatile int maximum;

    private String fileName;

    public ExportTenantsPortalSecretsDeferredProcess() {
        this.completed = false;
    }

    @Override
    public void execute() {
        completed = false;
        ReportTableFormatter formatter = new ReportTableXLSXFormatter();

        formatter.header("Address");
        formatter.header("City Zip");
        formatter.header("Unit");
        formatter.header("First");
        formatter.header("Middle");
        formatter.header("Last");
        formatter.header("Portal Registration Token");
        formatter.newRow();

        progress = 0;
        try {
            Persistence.service().startBackgroundProcessTransaction();

            final EntityQueryCriteria<Tenant> criteria = EntityQueryCriteria.create(Tenant.class);
            criteria.eq(criteria.proto().lease().status(), Lease.Status.Active);
            criteria.isNotNull(criteria.proto().customer().portalRegistrationToken());
            maximum = Persistence.service().count(criteria);

            ICursorIterator<Tenant> tenants = Persistence.service().query(null, criteria, AttachLevel.Attached);
            try {
                while (tenants.hasNext()) {
                    Tenant tenant = tenants.next();
                    Persistence.service().retrieveMember(tenant.lease());
                    Persistence.service().retrieveMember(tenant.lease().unit().building());
                    formatTenant(formatter, tenant);
                    ++progress;
                }
            } finally {
                tenants.close();
            }

        } finally {
            Persistence.service().endTransaction();
        }

        Downloadable d = new Downloadable(formatter.getBinaryData(), formatter.getContentType());
        fileName = VistaDeployment.getCurrentPmc().name().getValue() + "-tenants-portal-secrets.xlsx";
        d.save(fileName);
        completed = true;

    }

    public static TenantPortalAccessInformationDTO convert(Tenant tenant) {
        TenantPortalAccessInformationDTO dto = EntityFactory.create(TenantPortalAccessInformationDTO.class);
        dto.address().setValue(getAddress(tenant.lease().unit().building()));
        dto.cityZip().setValue(getCityZip(tenant.lease().unit().building()));
        dto.unit().setValue(tenant.lease().unit().info().number().getValue());
        dto.firstName().setValue(tenant.customer().person().name().firstName().getStringView());
        if (!tenant.customer().person().name().middleName().isNull()) {
            dto.middleName().setValue(tenant.customer().person().name().middleName().getStringView());
        } else {
            dto.middleName().setValue("");
        }
        dto.lastName().setValue(tenant.customer().person().name().lastName().getStringView());
        dto.portalRegistrationToken().setValue(tenant.customer().portalRegistrationToken().getValue());
        return dto;
    }

    private void formatTenant(ReportTableFormatter formatter, Tenant tenant) {
        TenantPortalAccessInformationDTO dto = convert(tenant);
        formatter.cell(dto.address().getValue());
        formatter.cell(dto.cityZip().getValue());
        formatter.cell(dto.unit().getValue());
        formatter.cell(dto.firstName().getValue());
        formatter.cell(dto.middleName().getValue());
        formatter.cell(dto.lastName().getValue());
        formatter.cell(dto.portalRegistrationToken().getValue());
        formatter.newRow();
    }

    private static String getAddress(Building building) {
        String address = "";
        address += building.info().address().streetNumber() != null ? building.info().address().streetNumber().getValue() + " " : "";
        address += building.info().address().streetName() != null ? building.info().address().streetName().getValue() + " " : "";
        address += building.info().address().streetType() != null ? building.info().address().streetType().getValue() : "";
        return address;
    }

    private static String getCityZip(Building building) {
        String cityZip = "";
        cityZip += building.info().address().city() != null ? building.info().address().city().getValue() + ", " : "";
        cityZip += building.info().address().province() != null ? building.info().address().province().name().getValue() + " " : "";
        cityZip += building.info().address().postalCode() != null ? building.info().address().postalCode().getValue() : "";
        return cityZip;
    }

    @Override
    public DeferredProcessProgressResponse status() {
        if (completed) {
            DeferredReportProcessProgressResponse r = new DeferredReportProcessProgressResponse();
            r.setCompleted();
            r.setDownloadLink(System.currentTimeMillis() + "/" + fileName);
            return r;
        } else {
            DeferredProcessProgressResponse r = super.status();
            r.setProgress(progress);
            r.setProgressMaximum(maximum);
            return r;
        }

    }
}
