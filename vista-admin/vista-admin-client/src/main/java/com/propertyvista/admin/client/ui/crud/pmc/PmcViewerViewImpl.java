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

import com.propertyvista.admin.client.ui.crud.AdminViewerViewImplBase;
import com.propertyvista.admin.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.admin.rpc.AdminSiteMap;
import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class PmcViewerViewImpl extends AdminViewerViewImplBase<PmcDTO> implements PmcViewerView {

    private final Button suspendBtn;

    private final Button activateBtn;

    private final Button cancelBtn;

    public PmcViewerViewImpl() {
        super(AdminSiteMap.Management.PMC.class, new PmcForm(true));

        Button upload = new Button("Upload import", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ImportUploadDialog d = new ImportUploadDialog(getForm().getValue());
                d.setDownloadServletPath(GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping);
                d.show();
            }
        });
        addHeaderToolbarTwoItem(upload.asWidget());

        Button downloadFull = new Button("Download export.xml", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.open(GWT.getModuleBaseURL() + "export.xml?pmc=" + getForm().getValue().getPrimaryKey(), null, null);
            }
        });
        addHeaderToolbarTwoItem(downloadFull.asWidget());

        Button downloadNoImages = new Button("Download export.xml (no images)", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.open(GWT.getModuleBaseURL() + "export.xml?pmc=" + getForm().getValue().getPrimaryKey() + "&images=false", null, null);
            }
        });
        addHeaderToolbarTwoItem(downloadNoImages.asWidget());

        Button resetCache = new Button("Reset Cache", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((PmcViewerView.Presenter) getPresenter()).resetCache();
            }
        });
        addHeaderToolbarTwoItem(resetCache);

        cancelBtn = new Button("Cancel PMC", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((PmcViewerView.Presenter) getPresenter()).cancelPmc();

            }
        });
        addHeaderToolbarTwoItem(cancelBtn);

        suspendBtn = new Button("Suspend", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((PmcViewerView.Presenter) getPresenter()).suspend();
            }
        });
        addHeaderToolbarTwoItem(suspendBtn);

        activateBtn = new Button("Activate", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((PmcViewerView.Presenter) getPresenter()).activate();

            }
        });
        addHeaderToolbarTwoItem(activateBtn);

    }

    @Override
    public void populate(PmcDTO value) {
        super.populate(value);

        suspendBtn.setVisible(value.status().getValue() == PmcStatus.Active);
        activateBtn.setVisible(value.status().getValue() != PmcStatus.Active);
        cancelBtn.setVisible(value.status().getValue() != PmcStatus.Cancelled);
    }

}