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
package com.propertyvista.portal.resident.activity.services.insurance;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.resources.tenantsure.TenantSureResources;
import com.propertyvista.portal.resident.ui.TermsView;

public class TenantSureFaqActivity extends AbstractActivity {

    private final TermsView view;

    public TenantSureFaqActivity(AppPlace place) {
        view = ResidentPortalSite.getViewFactory().instantiate(TermsView.class);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        // TODO VISTA-3596: consider getting the FAQ text from server
        view.populate(TenantSureResources.INSTANCE.faq().getText());
        panel.setWidget(view);
    }

}