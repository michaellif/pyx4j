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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

import com.pyx4j.forms.client.ui.CHyperlink;

import com.propertyvista.admin.client.ui.components.AdminViewersComponentFactory;
import com.propertyvista.admin.client.ui.crud.AdminViewerViewImplBase;
import com.propertyvista.admin.rpc.AdminSiteMap;
import com.propertyvista.admin.rpc.PmcDTO;

public class PmcViewerViewImpl extends AdminViewerViewImplBase<PmcDTO> implements PmcViewerView {

    public PmcViewerViewImpl() {
        super(AdminSiteMap.Properties.PMC.class, new PmcEditorForm(new AdminViewersComponentFactory()));

        CHyperlink upload = new CHyperlink(new Command() {
            @Override
            public void execute() {
                ImportUploadDialog.show(form.getValue());
            }
        });
        upload.setValue("Upload import.xml");
        addActionButton(upload.asWidget());

        CHyperlink download = new CHyperlink(new Command() {
            @Override
            public void execute() {
                Window.open(GWT.getModuleBaseURL() + "export.xml", null, null);
            }
        });
        download.setValue("Download export.xml");
        addActionButton(download.asWidget());

    }
}