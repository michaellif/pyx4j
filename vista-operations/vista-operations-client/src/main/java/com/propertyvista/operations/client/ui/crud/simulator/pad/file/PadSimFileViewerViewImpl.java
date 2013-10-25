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
package com.propertyvista.operations.client.ui.crud.simulator.pad.file;

import com.google.gwt.user.client.Command;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.ILister;
import com.pyx4j.site.client.ui.prime.lister.ListerInternalViewImplBase;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.operations.client.ui.crud.OperationsViewerViewImplBase;
import com.propertyvista.operations.client.ui.crud.simulator.pad.batch.PadSimBatchLister;
import com.propertyvista.operations.domain.payment.pad.simulator.PadSimBatch;
import com.propertyvista.operations.domain.payment.pad.simulator.PadSimFile;

public class PadSimFileViewerViewImpl extends OperationsViewerViewImplBase<PadSimFile> implements PadSimFileViewerView {

    private static final I18n i18n = I18n.get(PadSimFileEditorViewImpl.class);

    private final ILister<PadSimBatch> batchLister;

    Button replyAcknowledgment;

    Button replyReconciliation;

    Button createReturnReconciliation;

    Button replyReturns;

    public PadSimFileViewerViewImpl() {
        batchLister = new ListerInternalViewImplBase<PadSimBatch>(new PadSimBatchLister());

        setForm(new PadSimFileForm(this));

        replyAcknowledgment = new Button(i18n.tr("Reply Acknowledgment"), new Command() {
            @Override
            public void execute() {
                ((PadSimFileViewerView.Presenter) getPresenter()).replyAcknowledgment();
            }
        });
        addHeaderToolbarItem(replyAcknowledgment.asWidget());

        replyReconciliation = new Button(i18n.tr("Reply Reconciliation"), new Command() {
            @Override
            public void execute() {
                ((PadSimFileViewerView.Presenter) getPresenter()).replyReconciliation();
            }
        });
        addHeaderToolbarItem(replyReconciliation.asWidget());

        createReturnReconciliation = new Button(i18n.tr("Create Return File"), new Command() {
            @Override
            public void execute() {
                ((PadSimFileViewerView.Presenter) getPresenter()).createReturnReconciliation();
            }
        });
        addHeaderToolbarItem(createReturnReconciliation.asWidget());

        replyReturns = new Button(i18n.tr("Reply Returns"), new Command() {
            @Override
            public void execute() {
                ((PadSimFileViewerView.Presenter) getPresenter()).replyReturns();
            }
        });
        addHeaderToolbarItem(replyReturns.asWidget());

    }

    @Override
    public ILister<PadSimBatch> getBatchListerView() {
        return batchLister;
    }

    @Override
    public void populate(PadSimFile value) {
        super.populate(value);

        boolean returns = ((value != null) && (value.returns().getValue(Boolean.FALSE)));

        replyAcknowledgment.setVisible(!returns);
        replyReconciliation.setVisible(!returns);
        createReturnReconciliation.setVisible(!returns);
        replyReturns.setVisible(returns);

    }
}