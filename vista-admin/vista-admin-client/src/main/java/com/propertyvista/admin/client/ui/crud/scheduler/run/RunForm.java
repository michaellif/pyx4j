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
package com.propertyvista.admin.client.ui.crud.scheduler.run;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.domain.scheduler.Run;

public class RunForm extends AdminEntityForm<Run> {

    private static final I18n i18n = I18n.get(RunForm.class);

    public RunForm() {
        this(false);
    }

    public RunForm(boolean viewMode) {
        super(Run.class, viewMode);
    }

    @Override
    public void createTabs() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().trigger()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().trigger().triggerType()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().started()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().created()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().forDate()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status()), 15).build());

        content.setH2(++row, 0, 2, i18n.tr("Statistics"));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().stats().total()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().stats().processed()), 10).build());
        content.setWidget(row, 1, new DecoratorBuilder(inject(proto().stats().amountProcessed()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().stats().failed()), 10).build());
        content.setWidget(row, 1, new DecoratorBuilder(inject(proto().stats().amountFailed()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().stats().averageDuration()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().stats().totalDuration()), 10).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().stats().message()), 40).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().errorMessage()), 40).build());

        content.setH2(++row, 0, 2, i18n.tr("Data"));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);
        content.setWidget(++row, 0, ((RunViewerView) getParentView()).getRunDataListerView().asWidget());
        content.getFlexCellFormatter().setColSpan(row, 0, 2);

        selectTab(addTab(content, i18n.tr("General")));

    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        get(proto().errorMessage()).setVisible(!getValue().errorMessage().isNull());
    }
}