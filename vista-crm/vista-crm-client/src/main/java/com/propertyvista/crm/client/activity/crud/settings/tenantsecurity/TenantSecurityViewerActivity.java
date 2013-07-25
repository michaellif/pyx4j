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
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.settings.tenantsecurity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.essentials.rpc.report.ReportService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ReportDialog;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.settings.tenantsecurity.TenantSecurityView;
import com.propertyvista.crm.rpc.services.customer.ExportTenantsService;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class TenantSecurityViewerActivity extends AbstractActivity implements TenantSecurityView.Presenter {

    private final I18n i18n = I18n.get(TenantSecurityView.class);

    private final Place place;

    private final TenantSecurityView view;

    public TenantSecurityViewerActivity(Place place) {
        this.place = place;
        this.view = CrmSite.getViewFactory().instantiate(TenantSecurityView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        panel.setWidget(view);
    }

    @Override
    public void generatePortalSecurityCodes() {
        ReportDialog d = new ReportDialog(i18n.tr(""), i18n.tr("Preparing the list of tenant portal registration codes..."));
        d.setDownloadServletPath(GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping);
        d.start(GWT.<ReportService<?>> create(ExportTenantsService.class), null, null);
    }

    @Override
    public void populate() {
        // TODO Auto-generated method stub
    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub
    }

}
