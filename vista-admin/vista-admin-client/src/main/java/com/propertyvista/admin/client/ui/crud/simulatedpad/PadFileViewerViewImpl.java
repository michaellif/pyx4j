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
package com.propertyvista.admin.client.ui.crud.simulatedpad;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.admin.client.ui.crud.AdminViewerViewImplBase;
import com.propertyvista.admin.client.ui.crud.padsimulation.batch.PadBatchLister;
import com.propertyvista.admin.domain.payment.pad.sim.PadSimBatch;
import com.propertyvista.admin.domain.payment.pad.sim.PadSimFile;
import com.propertyvista.admin.rpc.AdminSiteMap;

public class PadFileViewerViewImpl extends AdminViewerViewImplBase<PadSimFile> implements PadFileViewerView {

    private static final I18n i18n = I18n.get(PadFileEditorViewImpl.class);

    private final IListerView<PadSimBatch> batchLister;

    public PadFileViewerViewImpl() {
        super(AdminSiteMap.Administration.PadSimulation.PadSimFile.class);

        batchLister = new ListerInternalViewImplBase<PadSimBatch>(new PadBatchLister());

        setForm(new PadFileForm(true));

        Button replyReconciliation = new Button(i18n.tr("Reply Reconciliation"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((PadFileViewerView.Presenter) presenter).replyReconciliation();
            }
        });
        addHeaderToolbarTwoItem(replyReconciliation.asWidget());

        Button replyAcknowledgment = new Button(i18n.tr("Reply Acknowledgment"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((PadFileViewerView.Presenter) presenter).replyAcknowledgment();
            }
        });
        addHeaderToolbarTwoItem(replyAcknowledgment.asWidget());

    }

    @Override
    public IListerView<PadSimBatch> getBatchListerView() {
        return batchLister;
    }
}