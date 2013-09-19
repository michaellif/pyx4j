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
package com.propertyvista.operations.client.ui.crud.pmc;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.client.ConfirmCommand;
import com.pyx4j.essentials.rpc.report.ReportService;
import com.pyx4j.site.client.ReportDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Button.ButtonMenuBar;

import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.operations.client.ui.crud.OperationsViewerViewImplBase;
import com.propertyvista.operations.client.ui.crud.scheduler.trigger.RunForDateDialog;
import com.propertyvista.operations.domain.scheduler.PmcProcessOptions;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.operations.rpc.dto.PmcDTO;
import com.propertyvista.operations.rpc.dto.PmcExportDownloadDTO;
import com.propertyvista.operations.rpc.services.ExportDownloadService;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class PmcViewerViewImpl extends OperationsViewerViewImplBase<PmcDTO> implements PmcViewerView {

    private final Button suspendBtn;

    private final Button activateBtn;

    private final Button cancelBtn;

    public PmcViewerViewImpl() {
        setForm(new PmcForm(this));

        Button upload = new Button("Upload import", new Command() {
            @Override
            public void execute() {
                ImportUploadDialog d = new ImportUploadDialog(getForm().getValue());
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
        download.setMenu(downloadMenu);
        addHeaderToolbarItem(download);

        Button runButton = new Button("Run Process");
        addHeaderToolbarItem(runButton);
        ButtonMenuBar runMenu = runButton.createMenu();
        runButton.setMenu(runMenu);
        for (final PmcProcessType pmcProcessType : EnumSet.allOf(PmcProcessType.class)) {
            if (!pmcProcessType.hasOption(PmcProcessOptions.GlobalOnly)) {

                if (!pmcProcessType.hasOption(PmcProcessOptions.RunForDay)) {
                    runMenu.addItem(pmcProcessType.getDescription(),
                            new ConfirmCommand("Confirm", "Do you really want to run process '" + pmcProcessType.getDescription() + "' ?", new Command() {
                                @Override
                                public void execute() {
                                    ((PmcViewerView.Presenter) getPresenter()).runProcess(pmcProcessType, null);
                                }
                            }));
                } else {
                    runMenu.addItem(pmcProcessType.getDescription(), new Command() {
                        @Override
                        public void execute() {

                            new RunForDateDialog() {
                                @Override
                                public boolean onClickOk() {
                                    ((PmcViewerView.Presenter) getPresenter()).runProcess(pmcProcessType, getValue());
                                    return true;
                                }
                            }.show();

                        }
                    });
                }
                ;
            }
        }

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
    public void setPresenter(IForm.Presenter presenter) {
        super.setPresenter(presenter);
        if (presenter != null) {
            ((PmcForm) getForm()).setOnboardingMerchantAccountsSource(((PmcViewerView.Presenter) presenter).getOnboardingMerchantAccountsSource());
            ((PmcForm) getForm()).setDirectDebitRecordsSource(((PmcViewerView.Presenter) presenter).getDirectDebitRecordsSource());
        }
    }

}