/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-22
 * @author stanp
 */
package com.propertyvista.operations.client.ui.crud.scheduler.run;

import com.google.gwt.user.client.Command;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.operations.client.ui.crud.OperationsViewerViewImplBase;
import com.propertyvista.operations.domain.scheduler.RunData;
import com.propertyvista.operations.domain.scheduler.RunDataStatus;

public class RunDataViewerViewImpl extends OperationsViewerViewImplBase<RunData> implements RunDataViewerView {

    private static final I18n i18n = I18n.get(RunDataViewerViewImpl.class);

    private final Button stopRun;

    public RunDataViewerViewImpl() {
        super(true);

        setForm(new RunDataForm(this));
        stopRun = new Button(i18n.tr("Cancel DataRun"), new Command() {
            @Override
            public void execute() {
                MessageDialog.confirm(i18n.tr("Cancel DataRun"), i18n.tr("Do you really want to cancel the Run?"), new Command() {
                    @Override
                    public void execute() {
                        ((RunDataViewerView.Presenter) getPresenter()).cancelDataRun();
                    }
                });
            }
        });
        addHeaderToolbarItem(stopRun.asWidget());
    }

    @Override
    public void populate(RunData value) {
        super.populate(value);
        stopRun.setVisible(value.status().getValue() == RunDataStatus.NeverRan);
    }
}