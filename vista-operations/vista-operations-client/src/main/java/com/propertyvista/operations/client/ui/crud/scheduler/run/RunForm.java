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
package com.propertyvista.operations.client.ui.crud.scheduler.run;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.operations.client.ui.components.OperationsEditorsComponentFactory;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.scheduler.ExecutionReport;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.rpc.TriggerDTO;

public class RunForm extends OperationsEntityForm<Run> {

    private static final I18n i18n = I18n.get(RunForm.class);

    private final ExecutionReportSectionLister reportSectionLister;

    public RunForm(IForm<Run> view) {
        super(Run.class, view);

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));
        int row = -1;

        content.setWidget(++row, 0,
                new FormDecoratorBuilder(inject(proto().trigger(), OperationsEditorsComponentFactory.createEntityHyperlink(TriggerDTO.class)), 40).build());

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().trigger().triggerType()), 40).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().created()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().forDate()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().started()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().startedBy()), 20).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().completed()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().status()), 15).build());

        content.setH2(++row, 0, 2, i18n.tr("Statistics"));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().executionReport().total()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().executionReport().processed()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().executionReport().failed()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().executionReport().erred()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().executionReport().averageDuration()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().executionReport().totalDuration()), 10).build());

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().executionReport().message()), 40).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().errorMessage()), 40).build());

        reportSectionLister = new ExecutionReportSectionLister();
        content.setH4(++row, 0, 2, i18n.tr("Details"));
        content.setWidget(++row, 0, reportSectionLister);

        content.setH2(++row, 0, 2, i18n.tr("Data"));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);
        content.setWidget(++row, 0, ((RunViewerView) getParentView()).getRunDataListerView().asWidget());
        content.getFlexCellFormatter().setColSpan(row, 0, 2);

        selectTab(addTab(content));

    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().errorMessage()).setVisible(!getValue().errorMessage().isNull());

        reportSectionLister.getDataSource().setParentFiltering(getValue().executionReport().getPrimaryKey(), ExecutionReport.class);
        reportSectionLister.restoreState();
    }
}