/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.ob.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.client.ClientEntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.common.client.site.VistaSite;
import com.propertyvista.ob.rpc.OnboardingSiteMap;
import com.propertyvista.ob.rpc.services.OnboardingAuthenticationService;

public class OnboardingSite extends VistaSite {

    public static final String ONBOARDING_INSERTION_ID = "vista.ob";

    static {
        ClientEntityFactory.ensureIEntityImplementations();
    }

    public OnboardingSite() {
        super("vista-onboarding", OnboardingSiteMap.class);
    }

    @Override
    public void onSiteLoad() {
        super.onSiteLoad();
        obtainAuthenticationData();
    }

    @Override
    public void showMessageDialog(String message, String title, String buttonText, Command command) {
        // TODO Auto-generated method stub
    }

    private void obtainAuthenticationData() {
        ClientContext.obtainAuthenticationData((GWT.<OnboardingAuthenticationService> create(OnboardingAuthenticationService.class)),
                new DefaultAsyncCallback<Boolean>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        hideLoadingIndicator();
                        super.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        hideLoadingIndicator();
                        initUI();
                    }

                });
    }

    private void initUI() {
        RootPanel root = RootPanel.get(ONBOARDING_INSERTION_ID);
        if (root == null) {
            root = RootPanel.get();
        }
        SimplePanel panel = new SimplePanel();
        root.add(panel);
        new OnboardingFlowSample(panel).doTestPmcCreationStep1();
    }
}
