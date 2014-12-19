/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-16
 * @author ArtyomB
 */
package com.propertyvista.crm.client.activity.crud.administration.tenantsecurity;

import java.io.Serializable;
import java.util.HashMap;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.rpc.report.ReportService;
import com.pyx4j.gwt.client.deferred.DeferredProcessDialog;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ReportDialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.administration.tenantsecurity.TenantSecurityView;
import com.propertyvista.crm.rpc.services.customer.EmailToTenantsService;
import com.propertyvista.crm.rpc.services.customer.ExportTenantsSecurityCodesService;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.tenant.access.PortalAccessSecutiryCodeReportType;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class TenantSecurityViewerActivity extends AbstractActivity implements TenantSecurityView.Presenter {

    private final I18n i18n = I18n.get(TenantSecurityView.class);

    private final TenantSecurityView view;

    public TenantSecurityViewerActivity(Place place) {
        this.view = CrmSite.getViewFactory().getView(TenantSecurityView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        panel.setWidget(view);
    }

    @Override
    public void generatePortalSecurityCodes(PortalAccessSecutiryCodeReportType type) {
        ReportDialog d = new ReportDialog(i18n.tr(""), i18n.tr("Preparing the list of tenant portal registration codes..."));
        d.setDownloadServletPath(GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping);

        HashMap<String, Serializable> parameters = new HashMap<String, Serializable>();
        parameters.put(ExportTenantsSecurityCodesService.PARAM_REPORT_TYPE, type);
        d.start(GWT.<ReportService<?>> create(ExportTenantsSecurityCodesService.class), null, parameters);
    }

    @Override
    public void sendMail(EmailTemplateType emailType) {

        @SuppressWarnings("rawtypes")
        EntityQueryCriteria<LeaseTermParticipant> criteria = EntityQueryCriteria.create(LeaseTermParticipant.class);
        criteria.eq(criteria.proto().leaseParticipant().customer().registeredInPortal(), false);
        criteria.isNotNull(criteria.proto().leaseParticipant().customer().person().email());

        GWT.<EmailToTenantsService> create(EmailToTenantsService.class).sendEmail(new DefaultAsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                showSendingDeferredProcess(result);
            }

        }, emailType, criteria);

    }

    private void showSendingDeferredProcess(String deferredCorrelationId) {
        DeferredProcessDialog d = new DeferredProcessDialog(i18n.tr("Processing"), i18n.tr("Preparing and sending E-mails..."), false) {
            @Override
            public void onDeferredSuccess(final DeferredProcessProgressResponse result) {
                super.onDeferredSuccess(result);
                hide();
                MessageDialog.info(result.getMessage());
            }
        };
        d.show();
        d.startProgress(deferredCorrelationId);
    }

    @Override
    public void populate() {
    }

    @Override
    public void refresh() {
    }

}
