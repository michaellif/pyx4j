/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-10
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;
import com.propertyvista.common.client.resources.FormImageBundle;

import com.pyx4j.essentials.client.ApplicationCommon;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.CaptchaComposite;
import com.pyx4j.widgets.client.GlassPanel;

public class VistaCrmSite extends AppSite {

    @Override
    public void onSiteLoad() {
        ImageFactory.setImageBundle((FormImageBundle) GWT.create(FormImageBundle.class));

        ApplicationCommon.initRpcGlassPanel();

        SiteGinjector ginjector = GWT.create(SiteGinjector.class);

        RootPanel.get().add(GlassPanel.instance());

        RootPanel.get().add(ginjector.getSiteView());

        CaptchaComposite.setPublicKey("6LfVZMESAAAAAJaoJgKeTN_F9CKs6_-XGqG4nsth");

        hideLoadingIndicator();

        //PtAppWizardManager.initWizard(ginjector);

    }

}
