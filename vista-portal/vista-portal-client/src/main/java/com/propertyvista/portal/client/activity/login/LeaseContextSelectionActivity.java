/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 24, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.activity.login;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.client.ui.LeaseContextSelectionView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;

public class LeaseContextSelectionActivity extends AbstractActivity implements LeaseContextSelectionView.Presenter {

    private final LeaseContextSelectionView view;

    public LeaseContextSelectionActivity() {
        this.view = PortalViewFactory.instance(LeaseContextSelectionView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        populate();
    }

    @Override
    public void populate() {

    }

    @Override
    public void setLeaseContext(Lease leaseStub) {

    }

}
