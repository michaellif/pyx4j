/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-03-03
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.fundstransfer.fundsreconciliationsummary;

import com.propertyvista.operations.client.ui.crud.OperationsViewerViewImplBase;
import com.propertyvista.operations.rpc.dto.FundsReconciliationSummaryDTO;

public class FundsReconciliationSummaryViewerViewImpl extends OperationsViewerViewImplBase<FundsReconciliationSummaryDTO> {

    public FundsReconciliationSummaryViewerViewImpl() {
        setForm(new FundsReconciliationSummaryForm(this));
    }

}
