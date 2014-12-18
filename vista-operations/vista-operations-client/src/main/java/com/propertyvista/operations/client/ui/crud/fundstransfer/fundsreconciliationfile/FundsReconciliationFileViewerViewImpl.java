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
 */
package com.propertyvista.operations.client.ui.crud.fundstransfer.fundsreconciliationfile;

import com.propertyvista.operations.client.ui.crud.OperationsViewerViewImplBase;
import com.propertyvista.operations.rpc.dto.FundsReconciliationFileDTO;

public class FundsReconciliationFileViewerViewImpl extends OperationsViewerViewImplBase<FundsReconciliationFileDTO> implements
        FundsReconciliationFileViewerView {

    private final FundsReconciliationSummaryLister summaryLister;

    public FundsReconciliationFileViewerViewImpl() {
        super(true);
        summaryLister = new FundsReconciliationSummaryLister();
        setForm(new FundsReconciliationFileForm(this));
    }

    @Override
    public void populate(FundsReconciliationFileDTO value) {
        super.populate(value);

        summaryLister.getDataSource().setParentEntityId(value.getPrimaryKey());
        summaryLister.populate();
    }

    @Override
    public FundsReconciliationSummaryLister getSummaryListerView() {
        return summaryLister;
    }
}
