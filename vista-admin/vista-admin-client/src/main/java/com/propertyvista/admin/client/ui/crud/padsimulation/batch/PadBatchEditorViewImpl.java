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
package com.propertyvista.admin.client.ui.crud.padsimulation.batch;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.admin.client.ui.crud.AdminEditorViewImplBase;
import com.propertyvista.admin.domain.payment.pad.sim.PadSimBatch;
import com.propertyvista.admin.rpc.AdminSiteMap;

public class PadBatchEditorViewImpl extends AdminEditorViewImplBase<PadSimBatch> implements PadBatchEditorView {

    private static final I18n i18n = I18n.get(PadBatchEditorViewImpl.class);

    public PadBatchEditorViewImpl() {
        super(AdminSiteMap.Administration.PadSimulation.PadSimBatch.class, new PadBatchForm());

        Button calculateAction = new Button(i18n.tr("Calculate Fields"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((PadBatchEditorView.Presenter) getPresenter()).calculate();
            }
        });
        addHeaderToolbarTwoItem(calculateAction.asWidget());
    }
}
