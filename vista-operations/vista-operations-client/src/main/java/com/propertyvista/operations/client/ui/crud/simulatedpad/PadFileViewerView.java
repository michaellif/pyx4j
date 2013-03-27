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
package com.propertyvista.operations.client.ui.crud.simulatedpad;

import com.pyx4j.site.client.ui.prime.form.IViewer;
import com.pyx4j.site.client.ui.prime.lister.ILister;

import com.propertyvista.operations.domain.payment.pad.sim.PadSimBatch;
import com.propertyvista.operations.domain.payment.pad.sim.PadSimFile;

public interface PadFileViewerView extends IViewer<PadSimFile> {

    interface Presenter extends IViewer.Presenter {

        public void replyAcknowledgment();

        public void replyReconciliation();

        public void replyReturns();

        public void createReturnReconciliation();
    }

    ILister<PadSimBatch> getBatchListerView();
}
