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

import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.site.client.AppSite;

public class VistaCrmSite extends AppSite {

    @Override
    public void onSiteLoad() {
        hideLoadingIndicator();

        Frame f = new Frame("http://www.anekdot.ru/id/438661/");
        f.setSize("100%", "200px");
        RootPanel.get().add(f);
    }

}
