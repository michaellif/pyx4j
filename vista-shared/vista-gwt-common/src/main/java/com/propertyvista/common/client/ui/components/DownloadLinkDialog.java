/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.site.client.NavigationUri;
import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.Dialog;

import com.propertyvista.portal.rpc.DeploymentConsts;

public class DownloadLinkDialog extends Dialog implements CancelOption {

    private final String downloadUrl;

    public DownloadLinkDialog(String caption, String message, String dowloadLinkCaption, String downloadUrl) {
        super(caption);
        setDialogOptions(this);

        this.downloadUrl = downloadUrl;

        VerticalPanel vp = new VerticalPanel();
        setBody(vp);
        vp.add(new HTML(message));
        String downloadServletPath = NavigationUri.getDeploymentBaseURL() + DeploymentConsts.downloadServletMapping;
        HTML downloadLink = new HTML("<a href=\"" + downloadServletPath + downloadUrl + "\" target=\"_blank\">" + dowloadLinkCaption + "</a>");
        downloadLink.ensureDebugId("reportDownloadLink");
        vp.add(downloadLink);
        downloadLink.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                DownloadLinkDialog.this.hide();
            }
        });
    }

    @Override
    public boolean onClickCancel() {
        //TODO
        //downloadService.cancelDownload(downloadUrl);
        return true;
    }

}
