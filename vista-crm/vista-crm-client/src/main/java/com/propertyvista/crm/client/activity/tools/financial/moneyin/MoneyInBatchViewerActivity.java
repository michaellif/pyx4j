/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.tools.financial.moneyin;

import java.io.Serializable;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.gwt.client.deferred.DeferredProcessDialog;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ReportDialog;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.tools.financial.moneyin.MoneyInBatchViewerView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;
import com.propertyvista.crm.rpc.services.financial.MoneyInBatchCrudService;
import com.propertyvista.crm.rpc.services.financial.MoneyInBatchDepositSlipPrintService;
import com.propertyvista.crm.rpc.services.financial.MoneyInToolService;
import com.propertyvista.domain.financial.PaymentPostingBatch.PostingStatus;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class MoneyInBatchViewerActivity extends CrmViewerActivity<MoneyInBatchDTO> implements MoneyInBatchViewerView.Presenter {

    private static final I18n i18n = I18n.get(MoneyInBatchViewerActivity.class);

    private boolean canPost;

    private boolean canCancel;

    public MoneyInBatchViewerActivity(CrudAppPlace place) {
        super(place, CrmSite.getViewFactory().getView(MoneyInBatchViewerView.class), GWT
                .<AbstractCrudService<MoneyInBatchDTO>> create(MoneyInBatchCrudService.class));

    }

    @Override
    public void showPaymentRecord(Key paymentRecordId) {
        AppSite.getPlaceController().goTo(new CrmSiteMap.Finance.Payment().formViewerPlace(paymentRecordId));
    }

    @Override
    public void createDownloadableDepositSlipPrintout() {
        ReportDialog reportDialog = new ReportDialog(i18n.tr("Creating Deposit Slip"), "");
        reportDialog.setDownloadServletPath(GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping);

        HashMap<String, Serializable> parameters = new HashMap<String, Serializable>();
        parameters.put(MoneyInBatchDepositSlipPrintService.PARAM_BATCH_PK, getEntityId().toString());

        ReportRequest request = new ReportRequest();
        request.setParameters(parameters);
        reportDialog.start(GWT.<MoneyInBatchDepositSlipPrintService> create(MoneyInBatchDepositSlipPrintService.class), request);
    }

    @Override
    public void postBatch() {
        GWT.<MoneyInToolService> create(MoneyInToolService.class).postPaymentBatch(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String deferredCorrelationId) {
                MoneyInBatchViewerActivity.this.startProcessingProgress(i18n.tr("Posting..."), deferredCorrelationId);
            }
        }, getEntityId());
    }

    @Override
    public void cancelBatch() {
        GWT.<MoneyInToolService> create(MoneyInToolService.class).cancelPaymentBatchPosting(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String deferredCorrelationId) {
                MoneyInBatchViewerActivity.this.startProcessingProgress(i18n.tr("Canceling Batch..."), deferredCorrelationId);
            }
        }, getEntityId());
    }

    @Override
    public boolean canEdit() {
        return false;
    }

    @Override
    public boolean canPost() {
        return canPost;
    }

    @Override
    public boolean canCancel() {
        return canCancel;
    }

    @Override
    protected void onPopulateSuccess(MoneyInBatchDTO result) {
        this.canPost = result.postingStatus().getValue() == PostingStatus.Created;
        this.canCancel = result.postingStatus().getValue() == PostingStatus.Created;
        super.onPopulateSuccess(result);
    }

    // TODO maybe this should be part of the View?
    private void startProcessingProgress(String message, String deferredCorrelationId) {
        DeferredProcessDialog d = new DeferredProcessDialog("", message, false) {
            @Override
            public void onDeferredSuccess(DeferredProcessProgressResponse result) {
                MoneyInBatchViewerActivity.this.populate();
                super.onDeferredSuccess(result);
            }

            @Override
            protected void onDeferredCompleate() {
                super.onDeferredCompleate();
                this.hide();
            }
        };
        d.show();
        d.startProgress(deferredCorrelationId);
    }

}
