/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author vlads
 * @version $Id: VistaTesterSite.java 36 2011-02-05 13:34:09Z michaellif $
 */
package com.propertyvista.portal.client.ptapp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.CaptchaComposite;

public class VistaPtApplicationSite extends AppSite {

    @Override
    public void onSiteLoad() {
        final SiteGinjector ginjector = GWT.create(SiteGinjector.class);
        RootPanel.get().add(ginjector.getSiteView());

        CaptchaComposite.setPublicKey("6LfVZMESAAAAAJaoJgKeTN_F9CKs6_-XGqG4nsth");

        hideLoadingIndicator();

        ClientContext.obtainAuthenticationData(new DefaultAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                ginjector.getPlaceHistoryHandler().handleCurrentHistory();
            }

            @Override
            public void onFailure(Throwable caught) {
                ginjector.getPlaceHistoryHandler().handleCurrentHistory();
                super.onFailure(caught);
            }
        });
    }
}
