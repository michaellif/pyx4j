/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.padsimulation.batch;

import com.google.gwt.user.client.Command;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.operations.client.ui.crud.OperationsEditorViewImplBase;
import com.propertyvista.operations.domain.payment.pad.sim.PadSimBatch;

public class PadBatchEditorViewImpl extends OperationsEditorViewImplBase<PadSimBatch> implements PadBatchEditorView {

    private static final I18n i18n = I18n.get(PadBatchEditorViewImpl.class);

    public PadBatchEditorViewImpl() {
        setForm(new PadBatchForm(this));

        Button calculateAction = new Button(i18n.tr("Calculate Fields"), new Command() {
            @Override
            public void execute() {
                ((PadBatchEditorView.Presenter) getPresenter()).calculate();
            }
        });
        addHeaderToolbarItem(calculateAction.asWidget());
    }
}
