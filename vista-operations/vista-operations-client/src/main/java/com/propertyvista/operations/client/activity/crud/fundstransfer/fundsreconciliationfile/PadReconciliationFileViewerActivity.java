/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.fundstransfer.fundsreconciliationfile;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.AbstractViewerActivity;
import com.pyx4j.site.client.activity.ListerController;
import com.pyx4j.site.client.ui.prime.lister.ILister;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.fundstransfer.fundsreconciliationfile.PadReconciliationFileViewerView;
import com.propertyvista.operations.rpc.dto.FundsReconciliationFileDTO;
import com.propertyvista.operations.rpc.dto.FundsReconciliationSummaryDTO;
import com.propertyvista.operations.rpc.services.PadReconciliationFileCrudService;
import com.propertyvista.operations.rpc.services.PadReconciliationSummaryListService;

public class PadReconciliationFileViewerActivity extends AbstractViewerActivity<FundsReconciliationFileDTO> implements PadReconciliationFileViewerView.Presenter {

    private final ILister.Presenter<?> summaryLister;

    public PadReconciliationFileViewerActivity(CrudAppPlace place) {
        super(place, OperationsSite.getViewFactory().getView(PadReconciliationFileViewerView.class), GWT
                .<AbstractCrudService<FundsReconciliationFileDTO>> create(PadReconciliationFileCrudService.class));

        summaryLister = new ListerController<FundsReconciliationSummaryDTO>(((PadReconciliationFileViewerView) getView()).getSummaryListerView(),
                GWT.<PadReconciliationSummaryListService> create(PadReconciliationSummaryListService.class), FundsReconciliationSummaryDTO.class);

    }

    @Override
    protected void onPopulateSuccess(FundsReconciliationFileDTO result) {
        super.onPopulateSuccess(result);

        summaryLister.setParent(result.getPrimaryKey());
        summaryLister.populate();
    }

    @Override
    public boolean canEdit() {
        return false;
    }
}
