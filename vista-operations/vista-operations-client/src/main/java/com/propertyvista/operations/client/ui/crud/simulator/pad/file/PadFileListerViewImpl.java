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
package com.propertyvista.operations.client.ui.crud.simulator.pad.file;

import com.google.gwt.user.client.Command;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.operations.client.ui.crud.OperationsListerViewImplBase;
import com.propertyvista.operations.domain.payment.pad.simulator.PadSimFile;

public class PadFileListerViewImpl extends OperationsListerViewImplBase<PadSimFile> implements PadFileListerView {

    private static final I18n i18n = I18n.get(PadFileListerViewImpl.class);

    public PadFileListerViewImpl() {
        setLister(new PadFileLister());

        // Add actions:
        Button loadPadFile = new Button(i18n.tr("Load"), new Command() {
            @Override
            public void execute() {
                ((PadFileListerView.Presenter) getPresenter()).loadPadFile();
            }
        });
        getLister().addActionItem(loadPadFile.asWidget());
    }
}
