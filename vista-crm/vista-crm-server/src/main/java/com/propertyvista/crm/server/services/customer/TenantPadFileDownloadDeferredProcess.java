/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-01
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer;

import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

public class TenantPadFileDownloadDeferredProcess extends AbstractDeferredProcess {

    private static final String TENANT_PAD_FILE = "tenant-pad-file.txt";

    public TenantPadFileDownloadDeferredProcess() {
        completed = false;
    }

    @Override
    public void execute() {
        // TODO Auto-generated method stub        
        Downloadable downloadable = new Downloadable("test".getBytes(), "text/html");
        downloadable.save(TENANT_PAD_FILE);
        completed = true;
    }

    @Override
    public DeferredProcessProgressResponse status() {
        if (completed) {
            DeferredReportProcessProgressResponse r = new DeferredReportProcessProgressResponse();
            r.setCompleted();
            r.setDownloadLink(System.currentTimeMillis() + "/" + TENANT_PAD_FILE);
            return r;
        } else {
            DeferredProcessProgressResponse r = super.status();
            r.setProgress(1);
            r.setProgressMaximum(1);
            return r;
        }
    }

}
