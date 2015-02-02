/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 31, 2014
 * @author stanp
 */
package com.propertyvista.crm.client.activity.crud.lease.eviction.n4;

import java.util.Vector;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
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
import com.propertyvista.crm.client.ui.crud.lease.eviction.n4.N4BatchViewerView;
import com.propertyvista.crm.client.ui.tools.common.LinkDialog;
import com.propertyvista.crm.rpc.services.legal.eviction.N4BatchCrudService;
import com.propertyvista.domain.legal.n4.N4Batch;
import com.propertyvista.dto.N4BatchDTO;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class N4BatchViewerActivity extends CrmViewerActivity<N4BatchDTO> implements N4BatchViewerView.Presenter {

    private static final I18n i18n = I18n.get(N4BatchViewerActivity.class);

    public N4BatchViewerActivity(CrudAppPlace place) {
        super(N4BatchDTO.class, place, CrmSite.getViewFactory().getView(N4BatchViewerView.class), GWT.<N4BatchCrudService> create(N4BatchCrudService.class));
    }

    @Override
    public void serviceBatch(N4BatchDTO batch) {
        ((N4BatchCrudService) getService()).serviceBatch(new DefaultAsyncCallback<String>() {
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
        }, EntityFactory.createIdentityStub(N4Batch.class, batch.getPrimaryKey()));
    }

    @Override
    public void downloadForms(N4BatchDTO batch) {
        Vector<Key> batchIds = new Vector<>();
        batchIds.add(batch.getPrimaryKey());
        ((N4BatchCrudService) getService()).downloadForms(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String deferredCorrelationId) {
                DeferredProcessDialog d = new DeferredProcessDialog(i18n.tr("N4 Document Download"), i18n.tr("Downloading Forms..."), false) {
                    @Override
                    public void onDeferredSuccess(final DeferredProcessProgressResponse result) {
                        super.onDeferredSuccess(result);
                        downloadForms((DeferredReportProcessProgressResponse) result);
                    }
                };
                d.show();
                d.startProgress(deferredCorrelationId);
            }
        }, batchIds);
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

    private void downloadForms(DeferredReportProcessProgressResponse response) {
        if (response.getDownloadLink() != null) {
            final String downloadUrl = GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping + "/" + response.getDownloadLink();
            new LinkDialog(i18n.tr("Download Forms"), i18n.tr("Click to Download"), downloadUrl) {
                @Override
                public boolean onClickCancel() {
                    GWT.<DownloadableService> create(DownloadableService.class).cancelDownload(null, downloadUrl);
                    return false;
                }
            }.show();
        }
    }
}
