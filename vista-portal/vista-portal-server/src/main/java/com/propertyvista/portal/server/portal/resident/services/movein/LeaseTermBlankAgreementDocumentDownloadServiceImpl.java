/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-01-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.resident.services.movein;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.server.report.ReportServiceImpl;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.portal.rpc.portal.resident.services.movein.LeaseTermBlankAgreementDocumentDownloadService;
import com.propertyvista.portal.server.portal.prospect.ProspectPortalContext;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;
import com.propertyvista.server.common.lease.LeaseTermBlankAgreementDocumentCreationProcess;

public class LeaseTermBlankAgreementDocumentDownloadServiceImpl extends ReportServiceImpl<IEntity> implements LeaseTermBlankAgreementDocumentDownloadService {

    @Override
    public void createDownload(AsyncCallback<String> callback, ReportRequest reportRequest) {
        Lease leaseIdStub = null;
        if (SecurityController.checkAnyBehavior(PortalResidentBehavior.values())) {
            leaseIdStub = ResidentPortalContext.getLeaseIdStub();
        } else if (SecurityController.checkAnyBehavior(PortalProspectBehavior.values())) {
            LeaseApplication masterApplication = ProspectPortalContext.getMasterOnlineApplication().leaseApplication();
            Persistence.ensureRetrieve(masterApplication.lease(), AttachLevel.IdOnly);
            leaseIdStub = masterApplication.lease().createIdentityStub();
        }

        if (leaseIdStub != null) {
            callback.onSuccess(DeferredProcessRegistry.fork(new LeaseTermBlankAgreementDocumentCreationProcess(leaseIdStub, true), ThreadPoolNames.DOWNLOADS));
        } else {
            throw new IllegalStateException("lease was not found");
        }
    }
}
