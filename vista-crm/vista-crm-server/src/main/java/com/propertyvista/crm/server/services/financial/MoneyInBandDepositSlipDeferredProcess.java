/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-20
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.financial;

import java.util.concurrent.atomic.AtomicInteger;

import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;

public class MoneyInBandDepositSlipDeferredProcess extends AbstractDeferredProcess {

    private static final long serialVersionUID = 1L;

    private final AtomicInteger progress;

    private final int progressMax;

    private final MoneyInBatchDTO batch;

    public MoneyInBandDepositSlipDeferredProcess(MoneyInBatchDTO batch) {
        this.progress = new AtomicInteger();
        this.progress.set(0);
        this.progressMax = 1;
        this.batch = batch;
    }

    @Override
    public void execute() {
//      pdf = new FileOutputStream(debugFileName(model.getDesignName(), ".pdf"));
//      JasperReportProcessor.createReport(model, JasperFileFormat.PDF, pdf);
//      pdf.flush();
        completed = true;
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse status = super.status();
        status.setProgress(progress.get());
        status.setProgressMaximum(progressMax);
        return status;
    }

}
