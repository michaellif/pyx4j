/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.field.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

import com.propertyvista.common.client.site.VistaSite;
import com.propertyvista.field.client.ui.FieldPanel;
import com.propertyvista.field.rpc.FiledSiteMap;

public class FieldSite extends VistaSite {

    public FieldSite() {
        super("vista-field", FiledSiteMap.class);
    }

    @Override
    public void onSiteLoad() {
        super.onSiteLoad();

        hideLoadingIndicator();

        RootPanel.get().add(RootLayoutPanel.get());

        RootLayoutPanel.get().add(new FieldPanel());
    }

    @Override
    public void showMessageDialog(String message, String title, String buttonText, Command command) {
    }

}
