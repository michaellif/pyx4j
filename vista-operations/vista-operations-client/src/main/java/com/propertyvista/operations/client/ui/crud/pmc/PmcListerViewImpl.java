/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.pmc;

import java.io.Serializable;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.rpc.report.ReportService;
import com.pyx4j.site.client.ReportDialog;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.operations.client.ui.crud.OperationsListerViewImplBase;
import com.propertyvista.operations.rpc.dto.PmcDTO;
import com.propertyvista.operations.rpc.services.DBIntegrityCheckService;
import com.propertyvista.operations.rpc.services.PmcDataReportService;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class PmcListerViewImpl extends OperationsListerViewImplBase<PmcDTO> implements PmcListerView {

    public PmcListerViewImpl() {
        setLister(new PmcLister());

        {
            Button downloadFull = new Button("QA Download images.csv", new Command() {
                @Override
                public void execute() {
                    EntityQueryCriteria<PmcDTO> criteria = EntityQueryCriteria.create(PmcDTO.class);
                    // TODO move to activity use and call ListerActivityBase.constructSearchCriteria()
                    HashMap<String, Serializable> params = new HashMap<String, Serializable>();
                    params.put(PmcDataReportService.LoadImagesParameter, Boolean.TRUE);

                    ReportDialog d = new ReportDialog("Report", "Creating report...");
                    d.setDownloadServletPath(GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping);
                    d.start(GWT.<ReportService<?>> create(PmcDataReportService.class), criteria, params);
                }
            });
            addHeaderToolbarItem(downloadFull);
        }

        {
            Button downloadFull = new Button("QA Download buildings.csv", new Command() {
                @Override
                public void execute() {
                    EntityQueryCriteria<PmcDTO> criteria = EntityQueryCriteria.create(PmcDTO.class);
                    // TODO move to activity use and call ListerActivityBase.constructSearchCriteria()
                    ReportDialog d = new ReportDialog("Report", "Creating report...");
                    d.setDownloadServletPath(GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping);
                    d.start(GWT.<ReportService<?>> create(PmcDataReportService.class), criteria);
                }
            });
            addHeaderToolbarItem(downloadFull);
        }

        {
            Button downloadFull = new Button("DB integrity check.csv", new Command() {
                @Override
                public void execute() {
                    EntityQueryCriteria<PmcDTO> criteria = EntityQueryCriteria.create(PmcDTO.class);

                    ReportDialog d = new ReportDialog("DB Summary", "Creating DB summary...");
                    d.setDownloadServletPath(GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping);
                    d.start(GWT.<ReportService<?>> create(DBIntegrityCheckService.class), criteria);
                }
            });
            addHeaderToolbarItem(downloadFull);
        }

    }
}
