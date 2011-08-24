/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.pmc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.essentials.client.upload.UploadPanel;
import com.pyx4j.essentials.rpc.upload.UploadService;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CLabel;

import com.propertyvista.admin.client.ui.components.AdminEditorsComponentFactory;
import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.admin.rpc.services.ImportUploadService;
import com.propertyvista.common.client.ClentNavigUtils;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class PmcEditorForm extends AdminEntityForm<PmcDTO> {

    public PmcEditorForm() {
        super(PmcDTO.class, new AdminEditorsComponentFactory());
    }

    public PmcEditorForm(IEditableComponentFactory factory) {
        super(PmcDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(inject(proto().name()), 15);
        main.add(inject(proto().dnsName(), new CLabel()), 15);

        main.setWidth("100%");

        // TODO MishaL Please do this Better
        // VladS Import export, Upload Experimental Stuff,
        CHyperlink download = new CHyperlink("Download", new Command() {
            @Override
            public void execute() {
                Window.open(GWT.getModuleBaseURL() + "export.xml", null, null);
            }
        });
        download.setValue("export.xml");
        main.add(download, 30);

        final UploadPanel uploadPanel = new UploadPanel((UploadService) GWT.create(ImportUploadService.class));
        uploadPanel.setVisible(false);
        uploadPanel.setSupportedExtensions("xml");
        uploadPanel.setServletPath(ClentNavigUtils.getDeploymentBaseURL() + DeploymentConsts.uploadServletMapping);

        CHyperlink upload = new CHyperlink(new Command() {
            @Override
            public void execute() {
                uploadPanel.setVisible(true);
            }
        });
        upload.setValue("Upload import.xml");
        main.add(upload, 30);
        main.add(uploadPanel.getForm());

        return main;
    }
}