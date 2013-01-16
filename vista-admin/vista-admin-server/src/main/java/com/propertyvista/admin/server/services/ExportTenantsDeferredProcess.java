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
package com.propertyvista.admin.server.services;

import java.util.List;
import java.util.concurrent.Callable;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.report.ReportTableCSVFormater;
import com.pyx4j.essentials.server.report.ReportTableFormater;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

import com.propertyvista.admin.rpc.PmcExportTenantsParamsDTO;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.server.jobs.TaskRunner;

public class ExportTenantsDeferredProcess extends AbstractDeferredProcess {

    /**
     *
     */
    private static final long serialVersionUID = 5026863365365592547L;

    private volatile int progress;

    private volatile int maximum;

    private String fileName;

    private final PmcExportTenantsParamsDTO params;

    public ExportTenantsDeferredProcess(PmcExportTenantsParamsDTO pmcExportTenantsParamsDTO) {
        completed = false;
        params = pmcExportTenantsParamsDTO;
    }

    @Override
    public void execute() {
        completed = false;
        ReportTableFormater formatter = new ReportTableCSVFormater();

        Persistence.service().startTransaction();
        Pmc pmc = TaskRunner.runInAdminNamespace(new Callable<Pmc>() {
            @Override
            public Pmc call() throws Exception {
                return Persistence.service().retrieve(Pmc.class, params.pmcKey().getValue());
            }
        });
        final EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
        List<Customer> customers = TaskRunner.runInTargetNamespace(pmc.namespace().getValue(), new Callable<List<Customer>>() {
            @Override
            public List<Customer> call() throws Exception {
                return Persistence.service().query(criteria);
            }
        });
        Persistence.service().endTransaction();

        formatter.header("FirstName");
        formatter.header("LastName");
        formatter.newRow();

        for (Customer customer : customers) {
            formatter.cell(customer.person().name().firstName().getValue());
            formatter.cell(customer.person().name().lastName().getValue());
            formatter.newRow();
        }
        Downloadable d = new Downloadable(formatter.getBinaryData(), formatter.getContentType());
        fileName = pmc.name().getValue() + "-tenants-without-portal.xls";
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
