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

import com.google.gwt.user.client.Command;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.operations.client.ui.crud.OperationsViewerViewImplBase;
import com.propertyvista.operations.client.ui.crud.padsimulation.batch.PadBatchLister;
import com.propertyvista.operations.domain.payment.pad.sim.PadSimBatch;
import com.propertyvista.operations.domain.payment.pad.sim.PadSimFile;

public class PadFileViewerViewImpl extends OperationsViewerViewImplBase<PadSimFile> implements PadFileViewerView {

    private static final I18n i18n = I18n.get(PadFileEditorViewImpl.class);

    private final IListerView<PadSimBatch> batchLister;

    public PadFileViewerViewImpl() {
        batchLister = new ListerInternalViewImplBase<PadSimBatch>(new PadBatchLister());

        setForm(new PadFileForm(this));

        Button replyReconciliation = new Button(i18n.tr("Reply Reconciliation"), new Command() {
            @Override
            public void execute() {
                ((PadFileViewerView.Presenter) getPresenter()).replyReconciliation();
            }
        });
        addHeaderToolbarItem(replyReconciliation.asWidget());

        Button replyAcknowledgment = new Button(i18n.tr("Reply Acknowledgment"), new Command() {
            @Override
            public void execute() {
                ((PadFileViewerView.Presenter) getPresenter()).replyAcknowledgment();
            }
        });
        addHeaderToolbarItem(replyAcknowledgment.asWidget());

    }

    @Override
    public IListerView<PadSimBatch> getBatchListerView() {
        return batchLister;
    }
}