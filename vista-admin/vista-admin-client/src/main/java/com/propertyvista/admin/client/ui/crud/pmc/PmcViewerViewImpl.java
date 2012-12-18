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

import java.io.Serializable;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.client.ReportDialog;
import com.pyx4j.essentials.rpc.report.ReportService;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.admin.client.ui.crud.AdminViewerViewImplBase;
import com.propertyvista.admin.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.admin.rpc.AdminSiteMap;
import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.admin.rpc.PmcExportDownloadDTO;
import com.propertyvista.admin.rpc.services.ExportDownloadService;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class PmcViewerViewImpl extends AdminViewerViewImplBase<PmcDTO> implements PmcViewerView {

    private final Button suspendBtn;

    private final Button activateBtn;

    private final Button cancelBtn;

    public PmcViewerViewImpl() {
        super(AdminSiteMap.Management.PMC.class);
        setForm(new PmcForm(this));

        Button upload = new Button("Upload import", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ImportUploadDialog d = new ImportUploadDialog(getForm().getValue());
                d.setDownloadServletPath(GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping);
                d.show();
            }
        });
        addHeaderToolbarItem(upload.asWidget());

        Button downloadFull = new Button("Download export.xml", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                PmcExportDownloadDTO request = EntityFactory.create(PmcExportDownloadDTO.class);
                request.pmcId().setValue(getForm().getValue().getPrimaryKey());
                request.exportImages().setValue(true);

                HashMap<String, Serializable> params = new HashMap<String, Serializable>();
                params.put(ExportDownloadService.pmcExportDownloadDTOParameter, request);
                ReportDialog d = new ReportDialog("Export", "Creating export with images...");
                d.setDownloadServletPath(GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping);
                d.start(GWT.<ReportService<?>> create(ExportDownloadService.class), null, params);
            }
        });
        addHeaderToolbarItem(downloadFull.asWidget());

        Button downloadNoImages = new Button("Download export.xml (no images)", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                PmcExportDownloadDTO request = EntityFactory.create(PmcExportDownloadDTO.class);
                request.pmcId().setValue(getForm().getValue().getPrimaryKey());
                request.exportImages().setValue(false);

                HashMap<String, Serializable> params = new HashMap<String, Serializable>();
                params.put(ExportDownloadService.pmcExportDownloadDTOParameter, request);
                ReportDialog d = new ReportDialog("Export", "Creating export...");
                d.setDownloadServletPath(GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping);
                d.start(GWT.<ReportService<?>> create(ExportDownloadService.class), null, params);
            }
        });
        addHeaderToolbarItem(downloadNoImages.asWidget());

        Button resetCache = new Button("Reset Cache", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((PmcViewerView.Presenter) getPresenter()).resetCache();
            }
        });
        addHeaderToolbarItem(resetCache);

        cancelBtn = new Button("Cancel PMC", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((PmcViewerView.Presenter) getPresenter()).cancelPmc();

            }
        });
        addHeaderToolbarItem(cancelBtn);

        suspendBtn = new Button("Suspend", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((PmcViewerView.Presenter) getPresenter()).suspend();
            }
        });
        addHeaderToolbarItem(suspendBtn);

        activateBtn = new Button("Activate", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((PmcViewerView.Presenter) getPresenter()).activate();

            }
        });

        addHeaderToolbarItem(activateBtn);

    }

    @Override
    public void populate(PmcDTO value) {
        super.populate(value);

        suspendBtn.setVisible(value.status().getValue() == PmcStatus.Active);
        activateBtn.setVisible(value.status().getValue() != PmcStatus.Active);
        cancelBtn.setVisible(value.status().getValue() != PmcStatus.Cancelled);
    }

    @Override
    public void setPresenter(com.pyx4j.site.client.ui.crud.form.IViewerView.Presenter presenter) {
        super.setPresenter(presenter);
        if (presenter != null) {
            ((PmcForm) getForm()).setOnboardingMerchantAccountsSource(((PmcViewerView.Presenter) presenter).getOnboardingMerchantAccountsSource());
        }
    }

}