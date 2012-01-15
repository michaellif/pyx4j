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
package com.propertyvista.admin.client.ui.crud.pmc;

import java.io.Serializable;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.client.ReportDialog;
import com.pyx4j.essentials.rpc.report.ReportService;
import com.pyx4j.forms.client.ui.CHyperlink;

import com.propertyvista.admin.client.ui.crud.AdminListerViewImplBase;
import com.propertyvista.admin.rpc.AdminSiteMap;
import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.admin.rpc.services.PmcDataReportService;

public class PmcListerViewImpl extends AdminListerViewImplBase<PmcDTO> implements PmcListerView {

    public PmcListerViewImpl() {
        super(AdminSiteMap.Management.PMC.class);
        setLister(new PmcLister());

        {
            CHyperlink downloadFull = new CHyperlink(new Command() {
                @Override
                public void execute() {
                    EntityQueryCriteria<PmcDTO> criteria = EntityQueryCriteria.create(PmcDTO.class);
                    // TODO move to activity use and call ListerActivityBase.constructSearchCriteria()
                    HashMap<String, Serializable> params = new HashMap<String, Serializable>();
                    params.put(PmcDataReportService.LoadImagesParameter, Boolean.TRUE);
                    ReportDialog.start((ReportService<?>) GWT.create(PmcDataReportService.class), criteria, params);
                }
            });
            downloadFull.setValue("QA Download images.csv");
            addActionButton(downloadFull.asWidget());
        }

        {
            CHyperlink downloadFull = new CHyperlink(new Command() {
                @Override
                public void execute() {
                    EntityQueryCriteria<PmcDTO> criteria = EntityQueryCriteria.create(PmcDTO.class);
                    // TODO move to activity use and call ListerActivityBase.constructSearchCriteria()
                    ReportDialog.start((ReportService<?>) GWT.create(PmcDataReportService.class), criteria);
                }
            });
            downloadFull.setValue("QA Download buildings.csv");
            addActionButton(downloadFull.asWidget());
        }

    }
}
