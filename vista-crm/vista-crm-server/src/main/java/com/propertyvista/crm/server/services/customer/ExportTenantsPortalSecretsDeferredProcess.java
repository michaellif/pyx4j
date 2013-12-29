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

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.report.EntityReportFormatter;
import com.pyx4j.essentials.server.report.ReportTableFormatter;
import com.pyx4j.essentials.server.report.ReportTableXLSXFormatter;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.tenant.access.PortalAccessSecutiryCodeReportType;
import com.propertyvista.domain.tenant.access.TenantPortalAccessInformationPerLeaseDTO;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.TenantPortalAccessInformationDTO;
import com.propertyvista.server.common.util.AddressRetriever;

public class ExportTenantsPortalSecretsDeferredProcess extends AbstractDeferredProcess {

    private static final long serialVersionUID = 5026863365365592547L;

    private final PortalAccessSecutiryCodeReportType reportType;

    private volatile int progress;

    private volatile int maximum;

    private String fileName;

    public ExportTenantsPortalSecretsDeferredProcess(PortalAccessSecutiryCodeReportType type) {
        this.reportType = type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute() {
        completed = false;
        ReportTableFormatter formatter = new ReportTableXLSXFormatter();

        @SuppressWarnings("rawtypes")
        EntityReportFormatter erf;
        switch (reportType) {
        case PerTenant:
            erf = new EntityReportFormatter<TenantPortalAccessInformationDTO>(TenantPortalAccessInformationDTO.class);
            break;
        case PerLease:
            erf = new EntityReportFormatter<TenantPortalAccessInformationPerLeaseDTO>(TenantPortalAccessInformationPerLeaseDTO.class);
            break;
        default:
            throw new IllegalArgumentException();
        }

        erf.createHeader(formatter);

        progress = 0;
        try {
            Persistence.service().startBackgroundProcessTransaction();

            final EntityQueryCriteria<Tenant> criteria = EntityQueryCriteria.create(Tenant.class);
            criteria.eq(criteria.proto().lease().status(), Lease.Status.Active);
            criteria.isNull(criteria.proto().lease().completion());
            criteria.isNotNull(criteria.proto().customer().portalRegistrationToken());
            criteria.eq(criteria.proto().lease().currentTerm().version().tenants().$().leaseParticipant().id(), criteria.proto().id());
            criteria.in(criteria.proto().lease().currentTerm().version().tenants().$().role(), LeaseTermParticipant.Role.portalAccess());
            criteria.eq(criteria.proto().lease().unit().building().suspended(), false);
            criteria.asc(criteria.proto().lease().unit().building());
            criteria.asc(criteria.proto().lease().unit());
            criteria.asc(criteria.proto().lease().leaseId());
            maximum = Persistence.service().count(criteria);

            Calendar nextMonthCalendar = new GregorianCalendar();
            nextMonthCalendar.setTimeInMillis(SystemDateManager.getTimeMillis());
            nextMonthCalendar.set(Calendar.DAY_OF_MONTH, 1);
            nextMonthCalendar.add(Calendar.MONTH, 2);
            LogicalDate moveOutNextMonthCutOff = new LogicalDate(nextMonthCalendar.getTime());

            Lease currentLease = null;
            TenantPortalAccessInformationPerLeaseDTO currentReportEntity = null;

            ICursorIterator<Tenant> tenants = Persistence.service().query(null, criteria, AttachLevel.Attached);
            try {
                while (tenants.hasNext()) {
                    Tenant tenant = tenants.next();
                    Persistence.service().retrieveMember(tenant.lease());

                    // Exclude Move Out
                    if (moveOutNextMonthCutOff.le(tenant.lease().expectedMoveOut().getValue())
                            || moveOutNextMonthCutOff.le(tenant.lease().actualMoveOut().getValue())) {
                        continue;
                    }

                    Persistence.service().retrieveMember(tenant.lease().unit().building());

                    switch (reportType) {
                    case PerLease:
                        if (tenant.lease().equals(currentLease)) {
                            addTenant(currentReportEntity, tenant);
                        } else {
                            if (currentReportEntity != null) {
                                erf.reportEntity(formatter, currentReportEntity);
                            }
                            currentReportEntity = convert(tenant).duplicate(TenantPortalAccessInformationPerLeaseDTO.class);
                            currentLease = tenant.lease();
                        }
                        break;
                    case PerTenant:
                        erf.reportEntity(formatter, convert(tenant));
                        break;
                    default:
                        throw new IllegalArgumentException();
                    }
                    ++progress;
                }

                if (currentReportEntity != null) {
                    erf.reportEntity(formatter, currentReportEntity);
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

    private void addTenant(TenantPortalAccessInformationDTO entity, Tenant tenant) {
        TenantPortalAccessInformationPerLeaseDTO dto = (TenantPortalAccessInformationPerLeaseDTO) entity;
        if (dto.tenantsCount().isNull()) {
            dto.tenantsCount().setValue(2);
        } else {
            dto.tenantsCount().setValue(dto.tenantsCount().getValue() + 1);
        }

        switch (dto.tenantsCount().getValue()) {
        case 2:
            dto.tenantNameFull2().setValue(tenant.customer().person().name().getStringView());
            dto.portalRegistrationToken2().setValue(tenant.customer().portalRegistrationToken().getValue());
            break;
        case 3:
            dto.tenantNameFull3().setValue(tenant.customer().person().name().getStringView());
            dto.portalRegistrationToken3().setValue(tenant.customer().portalRegistrationToken().getValue());
            break;
        case 4:
            dto.tenantNameFull4().setValue(tenant.customer().person().name().getStringView());
            dto.portalRegistrationToken4().setValue(tenant.customer().portalRegistrationToken().getValue());
            break;
        case 5:
            dto.tenantNameFull5().setValue(tenant.customer().person().name().getStringView());
            dto.portalRegistrationToken5().setValue(tenant.customer().portalRegistrationToken().getValue());
            break;
        case 6:
            dto.tenantNameFull6().setValue(tenant.customer().person().name().getStringView());
            dto.portalRegistrationToken6().setValue(tenant.customer().portalRegistrationToken().getValue());
            break;
        }

    }

    public static TenantPortalAccessInformationDTO convert(Tenant tenant) {
        TenantPortalAccessInformationDTO dto = EntityFactory.create(TenantPortalAccessInformationDTO.class);
        dto.leaseId().setValue(tenant.lease().leaseId().getValue());

        AddressStructured address = AddressRetriever.getLeaseLegalAddress(tenant.lease());
        dto.address().setValue(getAddressLine1(address));
        dto.cityZip().setValue(getCityZip(address));
        dto.city().setValue(address.city().getValue());
        dto.postalCode().setValue(address.postalCode().getValue());
        dto.province().setValue(address.province().getStringView());
        dto.unit().setValue(address.suiteNumber().getValue());
        dto.firstName().setValue(tenant.customer().person().name().firstName().getStringView());
        if (!tenant.customer().person().name().middleName().isNull()) {
            dto.middleName().setValue(tenant.customer().person().name().middleName().getStringView());
        } else {
            dto.middleName().setValue("");
        }
        dto.lastName().setValue(tenant.customer().person().name().lastName().getStringView());
        dto.tenantNameFull().setValue(tenant.customer().person().name().getStringView());

        dto.portalRegistrationBuiding().setValue(tenant.lease().unit().building().info().address().getStringView());
        dto.portalRegistrationToken().setValue(tenant.customer().portalRegistrationToken().getValue());
        return dto;
    }

    private static String getAddressLine1(AddressStructured address) {
        // This is fragment form AddressStructured @ToStringFormat
        return SimpleMessageFormat.format("{1} {2} {3}{4,choice,other#|null#|!null# {4}}{5,choice,null#|!null# {5}}", //
                "", address.streetNumber(),//
                address.streetNumberSuffix(),//
                address.streetName(),//
                address.streetType().getValue(),//
                address.streetDirection());
    }

    private static String getCityZip(AddressStructured address) {
        return SimpleMessageFormat.format("{0,choice,null#|!null#{0}, }{1,choice,null#|!null# {1}}{2,choice,null#|!null# {2}}", //
                address.city(),//
                address.province().name(), //
                address.postalCode());
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
