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
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.client.ReportDialog;
import com.pyx4j.essentials.rpc.report.ReportService;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Button.ButtonMenuBar;

import com.propertyvista.admin.client.ui.crud.AdminViewerViewImplBase;
import com.propertyvista.admin.rpc.AdminSiteMap;
import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.admin.rpc.PmcExportDownloadDTO;
import com.propertyvista.admin.rpc.PmcExportTenantsParamsDTO;
import com.propertyvista.admin.rpc.services.ExportDownloadService;
import com.propertyvista.admin.rpc.services.ExportTenantsService;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class PmcViewerViewImpl extends AdminViewerViewImplBase<PmcDTO> implements PmcViewerView {

    private final Button suspendBtn;

    private final Button activateBtn;

    private final Button cancelBtn;

    public PmcViewerViewImpl() {
        super(AdminSiteMap.Management.PMC.class);
        setForm(new PmcForm(this));

        Button upload = new Button("Upload import", new Command() {
            @Override
            public void execute() {
                ImportUploadDialog d = new ImportUploadDialog(getForm().getValue());
                d.setDownloadServletPath(GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping);
                d.show();
            }
        });
        addHeaderToolbarItem(upload.asWidget());

        Button download = new Button("Download");
        ButtonMenuBar downloadMenu = download.createMenu();
        downloadMenu.addItem("export.xml (full)", new Command() {
            @Override
            public void execute() {
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
        downloadMenu.addItem("export.xml (no images)", new Command() {
            @Override
            public void execute() {
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
        downloadMenu.addItem("yardi tenants", new Command() {

            @Override
            public void execute() {
                PmcExportTenantsParamsDTO params = EntityFactory.create(PmcExportTenantsParamsDTO.class);
                params.pmcKey().setValue(getForm().getValue().getPrimaryKey());

                HashMap<String, Serializable> paramsHolder = new HashMap<String, Serializable>();
                paramsHolder.put(ExportTenantsService.PARAMS_KEY, params);

                ReportDialog d = new ReportDialog("Exporting Yardi Tenants", "Exporting Tenants...");
                d.setDownloadServletPath(GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping);
                d.start(GWT.<ReportService<?>> create(ExportTenantsService.class), null, paramsHolder);
            }

        });
        download.setMenu(downloadMenu);
        addHeaderToolbarItem(download);

        Button resetCache = new Button("Reset Cache", new Command() {

            @Override
            public void execute() {
                ((PmcViewerView.Presenter) getPresenter()).resetCache();
            }
        });
        addHeaderToolbarItem(resetCache);

        cancelBtn = new Button("Cancel PMC", new Command() {

            @Override
            public void execute() {
                ((PmcViewerView.Presenter) getPresenter()).cancelPmc();

            }
        });
        addHeaderToolbarItem(cancelBtn);

        suspendBtn = new Button("Suspend", new Command() {

            @Override
            public void execute() {
                ((PmcViewerView.Presenter) getPresenter()).suspend();
            }
        });
        addHeaderToolbarItem(suspendBtn);

        activateBtn = new Button("Activate", new Command() {

            @Override
            public void execute() {
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