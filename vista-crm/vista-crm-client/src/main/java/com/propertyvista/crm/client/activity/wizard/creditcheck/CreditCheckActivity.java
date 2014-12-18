/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-08
 * @author ArtyomB
 */
package com.propertyvista.crm.client.activity.wizard.creditcheck;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.vista2pmc.CreditCheckStatusService;
import com.propertyvista.domain.pmc.PmcEquifaxStatus;

/**
 * This activity's job is to dispach the CRM user to correct place depending on the status of the credit check: either to a wizard place to setup up a new
 * account or to a viewer to see the status of credit check.
 */
public class CreditCheckActivity extends AbstractActivity {

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        GWT.<CreditCheckStatusService> create(CreditCheckStatusService.class).obtainStatus(new DefaultAsyncCallback<PmcEquifaxStatus>() {

            @Override
            public void onSuccess(PmcEquifaxStatus result) {
                if (result == PmcEquifaxStatus.NotRequested) {
                    AppSite.getPlaceController().goTo(new CrmSiteMap.Administration.Settings.CreditCheck.Setup());
                } else {
                    AppSite.getPlaceController().goTo(new CrmSiteMap.Administration.Settings.CreditCheck.Status().formViewerPlace(new Key(-1)));
                }
            }

        });
    }

}
