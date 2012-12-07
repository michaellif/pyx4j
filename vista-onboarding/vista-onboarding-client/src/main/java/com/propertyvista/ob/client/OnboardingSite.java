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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

import com.propertyvista.common.client.VistaSite;
import com.propertyvista.portal.rpc.ob.OnboardingSiteMap;

public class OnboardingSite extends VistaSite {

    public OnboardingSite() {
        super("vista-onboarding", OnboardingSiteMap.class);
    }

    @Override
    public void onSiteLoad() {
        super.onSiteLoad();

        hideLoadingIndicator();

        RootPanel.get().add(new Button("Start Here"));
    }

    @Override
    public void showMessageDialog(String message, String title, String buttonText, Command command) {
        // TODO Auto-generated method stub
    }

}
