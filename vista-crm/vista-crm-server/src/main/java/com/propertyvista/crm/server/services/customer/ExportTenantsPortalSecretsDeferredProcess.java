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

import java.util.List;
import java.util.concurrent.Callable;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.report.ReportTableFormater;
import com.pyx4j.essentials.server.report.ReportTableXLSXFormater;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.server.jobs.TaskRunner;

public class ExportTenantsPortalSecretsDeferredProcess extends AbstractDeferredProcess {

    /**
     *
     */
    private static final long serialVersionUID = 5026863365365592547L;

    private volatile int progress;

    private volatile int maximum;

    private String fileName;

    private final Pmc pmc;

    public ExportTenantsPortalSecretsDeferredProcess(Pmc pmc) {
        this.completed = false;
        this.pmc = pmc;
    }

    @Override
    public void execute() {
        completed = false;
        ReportTableFormater formatter = new ReportTableXLSXFormater();

        Persistence.service().startTransaction();

        final EntityQueryCriteria<Tenant> criteria = EntityQueryCriteria.create(Tenant.class);
        criteria.eq(criteria.proto().lease().status(), Lease.Status.Active);
        criteria.isNotNull(criteria.proto().customer().portalRegistrationToken());
        List<Tenant> tenants = TaskRunner.runInTargetNamespace(pmc.namespace().getValue(), new Callable<List<Tenant>>() {
            @Override
            public List<Tenant> call() throws Exception {
                List<Tenant> tenants = Persistence.service().query(criteria);
                for (Tenant tenant : tenants) {
                    Persistence.service().retrieveMember(tenant.lease());
                    Persistence.service().retrieveMember(tenant.lease().unit().building());
                }
                return tenants;
            }
        });
        Persistence.service().endTransaction();

        formatter.header("Address");
        formatter.header("Unit");
        formatter.header("Tenant Name");
        formatter.header("Portal Registration Token");
        formatter.newRow();

        maximum = tenants.size();
        progress = 0;
        for (Tenant tenant : tenants) {
            formatter.cell(tenant.lease().unit().building().info().address().getStringView());
            formatter.cell(tenant.lease().unit().info().number().getValue());
            formatter.cell(tenant.customer().person().name().getStringView());
            formatter.cell(tenant.customer().portalRegistrationToken().getValue());
            formatter.newRow();
            ++progress;
        }
        Downloadable d = new Downloadable(formatter.getBinaryData(), formatter.getContentType());
        fileName = pmc.name().getValue() + "-tenants-portal-secrets.xlsx";
        d.save(fileName);
        completed = true;

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
