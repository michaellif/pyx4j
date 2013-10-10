/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-10
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity.services.insurance;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.rpc.portal.web.services.services.TenantSureInsurancePolicyCrudService;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.resources.tenantsure.TenantSureResources;
import com.propertyvista.portal.web.client.ui.TermsView;

public class TenantSureFaqActivity extends AbstractActivity {

    private final TenantSureInsurancePolicyCrudService service;

    private final TermsView view;

    public TenantSureFaqActivity(AppPlace place) {
        service = GWT.create(TenantSureInsurancePolicyCrudService.class);
        view = PortalWebSite.getViewFactory().instantiate(TermsView.class);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        if (false) {
            // TODO for now we keep TenantSure FAQ in the resource
            service.getFaq(new DefaultAsyncCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    view.populate(result);
                    panel.setWidget(view);
                }
            });
        }
        view.populate(TenantSureResources.INSTANCE.faq().getText());
        panel.setWidget(view);
    }

}