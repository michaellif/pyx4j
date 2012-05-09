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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.domain.scheduler.Run;

public class RunEditorForm extends AdminEntityForm<Run> {

    private static final I18n i18n = I18n.get(RunEditorForm.class);

    public RunEditorForm() {
        this(false);
    }

    public RunEditorForm(boolean viewMode) {
        super(Run.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel container = new FormFlexPanel();
        int row = -1;

        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().trigger()), 15).build());
        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().trigger().triggerType()), 15).build());
        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().created()), 10).build());
        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status()), 15).build());

        container.setH2(++row, 0, 2, i18n.tr("Statistics"));
        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().stats().total()), 10).build());
        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().stats().processed()), 10).build());
        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().stats().failed()), 10).build());
        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().stats().averageDuration()), 10).build());
        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().stats().totalDuration()), 10).build());
        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().stats().updateTime()), 10).build());

        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().errorMessage()), 40).build());

        container.setH2(++row, 0, 2, i18n.tr("Data"));
        container.setWidget(++row, 0, ((RunViewerView) getParentView()).getRunDataListerView().asWidget());

        return container;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        get(proto().errorMessage()).setVisible(!getValue().errorMessage().isNull());
    }
}