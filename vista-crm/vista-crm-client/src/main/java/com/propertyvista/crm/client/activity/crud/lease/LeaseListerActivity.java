/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.lease;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.essentials.rpc.report.ReportService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.ReportDialog;

import com.propertyvista.common.client.ui.components.UploadDialogBase;
import com.propertyvista.common.client.ui.components.UploadResponseDownloadableReciver;
import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.lease.common.LeaseListerActivityBase;
import com.propertyvista.crm.client.ui.crud.lease.LeaseListerView;
import com.propertyvista.crm.rpc.services.customer.TenantPadFileDownloadService;
import com.propertyvista.crm.rpc.services.customer.TenantPadFileUploadService;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class LeaseListerActivity extends LeaseListerActivityBase<LeaseDTO> implements LeaseListerView.Presenter {

    private static final I18n i18n = I18n.get(LeaseListerActivity.class);

    public LeaseListerActivity(Place place) {
        super(place, CrmSite.getViewFactory().getView(LeaseListerView.class), GWT.<LeaseViewerCrudService> create(LeaseViewerCrudService.class), LeaseDTO.class);
        ((LeaseListerView) getView()).setPadFileControlsEnabled(SecurityController.checkBehavior(VistaCrmBehavior.PropertyVistaSupport));
    }

    @Override
    public void uploadPadFile() {
        UploadDialogBase<IEntity> dialog = new UploadDialogBase<IEntity>(i18n.tr("Upload PAD File"),
                GWT.<TenantPadFileUploadService> create(TenantPadFileUploadService.class));
        dialog.setUploadReciver(new UploadResponseDownloadableReciver(i18n.tr("PAD Upload")));
        dialog.show();
    }

    @Override
    public void downloadPadFile() {
        ReportDialog d = new ReportDialog(i18n.tr(""), i18n.tr("Generating Tenant's PAD file..."));
        d.setDownloadServletPath(GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping);
        d.start(GWT.<ReportService<?>> create(TenantPadFileDownloadService.class), null, null);
    }
}
