/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.padsimulation;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.admin.client.ui.crud.AdminListerViewImplBase;
import com.propertyvista.admin.domain.payment.pad.sim.PadSimFile;
import com.propertyvista.admin.rpc.AdminSiteMap;

public class PadFileListerViewImpl extends AdminListerViewImplBase<PadSimFile> implements PadFileListerView {

    private static final I18n i18n = I18n.get(PadFileListerViewImpl.class);

    public PadFileListerViewImpl() {
        super(AdminSiteMap.Administration.PadSimulation.PadSimFile.class);
        setLister(new PadFileLister());

        // Add actions:
        Button loadPadFile = new Button(i18n.tr("Load"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((PadFileListerView.Presenter) getPresenter()).loadPadFile();
            }
        });
        getLister().addActionItem(loadPadFile.asWidget());
    }
}
