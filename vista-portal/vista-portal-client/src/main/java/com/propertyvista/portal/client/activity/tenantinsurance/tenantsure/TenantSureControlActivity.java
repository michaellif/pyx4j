/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-13
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.activity.tenantinsurance.tenantsure;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TenantSureManagementlView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;

public class TenantSureControlActivity extends AbstractActivity implements TenantSureManagementlView.Presenter {

    private final TenantSureManagementlView view;

    public TenantSureControlActivity() {
        view = PortalViewFactory.instance(TenantSureManagementlView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

}
