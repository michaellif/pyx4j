/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2014
 * @author ernestog
 */
package com.propertyvista.operations.client.activity.crud.tools.oapi;

import java.io.Serializable;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.essentials.rpc.report.ReportService;
import com.pyx4j.site.client.ReportDialog;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.activity.crud.AdminViewerActivity;
import com.propertyvista.operations.client.ui.crud.tools.oapi.OapiViewerView;
import com.propertyvista.operations.rpc.dto.OapiConversionDTO;
import com.propertyvista.operations.rpc.services.OapiCrudService;
import com.propertyvista.operations.rpc.services.tools.oapi.OapiXMLFileDownloadService;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class OapiViewerActivity extends AdminViewerActivity<OapiConversionDTO> implements OapiViewerView.Presenter {

    public OapiViewerActivity(CrudAppPlace place) {
        super(OapiConversionDTO.class, place, OperationsSite.getViewFactory().getView(OapiViewerView.class), GWT
                .<AbstractCrudService<OapiConversionDTO>> create(OapiCrudService.class));
    }

    @Override
    public void downloadXMLFile() {
        OapiConversionDTO entity = EntityFactory.create(OapiConversionDTO.class);
        entity.id().setValue(getEntityId());

        HashMap<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(OapiXMLFileDownloadService.OAPIExportDownloadDTOPKParameter, entity);

        ReportDialog d = new ReportDialog("Download XML file", "Generating file...");
        d.setDownloadServletPath(GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping);
        d.start(GWT.<ReportService<?>> create(OapiXMLFileDownloadService.class), null, params);
    }
}
