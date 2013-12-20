/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.tools.financial.moneyin;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.tools.financial.moneyin.MoneyInBatchView;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;
import com.propertyvista.crm.rpc.services.financial.MoneyInBatchCrudService;

public class MoneyInBatchViewerActivity extends CrmViewerActivity<MoneyInBatchDTO> implements MoneyInBatchView.Presenter {

    public MoneyInBatchViewerActivity(CrudAppPlace place) {
        super(place, CrmSite.getViewFactory().getView(MoneyInBatchView.class), GWT.<AbstractCrudService<MoneyInBatchDTO>> create(MoneyInBatchCrudService.class));
    }

    @Override
    public void createDownloadableDepositSlipPrintout() {
//        DeferredReportProcessProgressResponse response = (DeferredReportProcessProgressResponse) result;
//        String downloadUrl = GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping + "/" + response.getDownloadLink();
//        new ReportDialog(i18n.tr("Creating Money In Deposit Printout"), );
    }

    @Override
    public void cancelPrintOutGeneration(String downloadUrl) {
        // TODO Auto-generated method stub

    }

}
