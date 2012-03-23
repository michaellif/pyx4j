/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.pmc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;

import com.pyx4j.widgets.client.Button;

import com.propertyvista.admin.client.activity.crud.pmc.PmcViewerActivity;
import com.propertyvista.admin.client.ui.crud.AdminViewerViewImplBase;
import com.propertyvista.admin.rpc.AdminSiteMap;
import com.propertyvista.admin.rpc.PmcDTO;

public class PmcViewerViewImpl extends AdminViewerViewImplBase<PmcDTO> implements PmcViewerView {

    public PmcViewerViewImpl() {
        super(AdminSiteMap.Management.PMC.class, new PmcEditorForm(true));

        Button upload = new Button("Upload import", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ImportUploadDialog.show(form.getValue());
            }
        });
        addToolbarItem(upload.asWidget());

        Button downloadFull = new Button("Download export.xml", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.open(GWT.getModuleBaseURL() + "export.xml?pmc=" + getForm().getValue().getPrimaryKey(), null, null);
            }
        });
        addToolbarItem(downloadFull.asWidget());

        Button downloadNoImages = new Button("Download export.xml (no images)", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.open(GWT.getModuleBaseURL() + "export.xml?pmc=" + getForm().getValue().getPrimaryKey() + "&images=false", null, null);
            }
        });
        addToolbarItem(downloadNoImages.asWidget());

        Button resetCache = new Button("Reset Cache", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((PmcViewerActivity) presenter).resetCache();
            }
        });
        addToolbarItem(resetCache);
    }
}