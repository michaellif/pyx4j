/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 22, 2014
 * @author stanp
 */
package com.propertyvista.crm.client.activity.crud.lease.eviction;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.essentials.rpc.download.DownloadableService;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.gwt.client.deferred.DeferredProcessDialog;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.lease.eviction.EvictionCaseViewerView;
import com.propertyvista.crm.client.ui.tools.common.LinkDialog;
import com.propertyvista.crm.rpc.services.legal.eviction.EvictionCaseCrudService;
import com.propertyvista.dto.EvictionCaseDTO;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class EvictionCaseViewerActivity extends CrmViewerActivity<EvictionCaseDTO> implements EvictionCaseViewerView.Presenter {

    private static final I18n i18n = I18n.get(EvictionCaseViewerActivity.class);

    public EvictionCaseViewerActivity(CrudAppPlace place) {
        super(EvictionCaseDTO.class, place, CrmSite.getViewFactory().getView(EvictionCaseViewerView.class), GWT
                .<EvictionCaseCrudService> create(EvictionCaseCrudService.class));
    }

    @Override
    public void issueN4(EvictionCaseDTO evictionCase) {
        ((EvictionCaseCrudService) getService()).issueN4(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String deferredCorrelationId) {
                DeferredProcessDialog d = new DeferredProcessDialog(i18n.tr("N4 Document Generation"), i18n.tr("Generating Forms..."), false) {
                    @Override
                    public void onDeferredSuccess(final DeferredProcessProgressResponse result) {
                        super.onDeferredSuccess(result);
                        downloadErrorReport((DeferredReportProcessProgressResponse) result);
                        populate();
                    }
                };
                d.show();
                d.startProgress(deferredCorrelationId);
            }
        }, EntityFactory.createIdentityStub(EvictionCaseDTO.class, evictionCase.getPrimaryKey()));
    }

    private void downloadErrorReport(DeferredReportProcessProgressResponse response) {
        if (response.getDownloadLink() != null) {
            final String downloadUrl = GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping + "/" + response.getDownloadLink();
            new LinkDialog(i18n.tr("Errors Occurred"), i18n.tr("Download Error Report"), downloadUrl) {
                @Override
                public boolean onClickCancel() {
                    GWT.<DownloadableService> create(DownloadableService.class).cancelDownload(null, downloadUrl);
                    return false;
                }
            }.show();
        }
    }
}
