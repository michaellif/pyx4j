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
package com.propertyvista.operations.client.ui.crud.fundstransfer.fundsreconciliationfile;

import com.pyx4j.site.client.ui.prime.lister.ILister;
import com.pyx4j.site.client.ui.prime.lister.ListerInternalViewImplBase;

import com.propertyvista.operations.client.ui.crud.OperationsViewerViewImplBase;
import com.propertyvista.operations.rpc.dto.PadReconciliationFileDTO;
import com.propertyvista.operations.rpc.dto.PadReconciliationSummaryDTO;

public class PadReconciliationFileViewerViewImpl extends OperationsViewerViewImplBase<PadReconciliationFileDTO> implements PadReconciliationFileViewerView {

    private final ILister<PadReconciliationSummaryDTO> summaryLister;

    public PadReconciliationFileViewerViewImpl() {
        super(true);
        summaryLister = new ListerInternalViewImplBase<PadReconciliationSummaryDTO>(new PadReconciliationSummaryLister());
        setForm(new PadReconciliationFileForm(this));
    }

    @Override
    public ILister<PadReconciliationSummaryDTO> getSummaryListerView() {
        return summaryLister;
    }
}
