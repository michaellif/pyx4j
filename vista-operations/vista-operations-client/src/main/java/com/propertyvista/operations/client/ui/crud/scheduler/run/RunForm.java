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
import com.propertyvista.operations.rpc.dto.TriggerDTO;

public class RunForm extends OperationsEntityForm<Run> {

    private static final I18n i18n = I18n.get(RunForm.class);

    private final ExecutionReportSectionLister reportSectionLister;

    public RunForm(IForm<Run> view) {
        super(Run.class, view);

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0,
                inject(proto().trigger(), OperationsEditorsComponentFactory.createEntityHyperlink(TriggerDTO.class), new FormDecoratorBuilder().build()));

        content.setWidget(row, 1, inject(proto().created(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().trigger().triggerType(), new FormDecoratorBuilder().build()));
        content.setWidget(row, 1, inject(proto().forDate(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().startedBy(), new FormDecoratorBuilder().build()));
        content.setWidget(row, 1, inject(proto().started(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().status(), new FormDecoratorBuilder(15).build()));
        content.setWidget(row, 1, inject(proto().completed(), new FormDecoratorBuilder(10).build()));

        content.setH2(++row, 0, 2, i18n.tr("Statistics"));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);
        content.setWidget(++row, 0, inject(proto().executionReport().total(), new FormDecoratorBuilder(10).build()));
        content.setWidget(row, 1, inject(proto().executionReport().averageDuration(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().executionReport().processed(), new FormDecoratorBuilder(10).build()));
        content.setWidget(row, 1, inject(proto().executionReport().totalDuration(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().executionReport().failed(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().executionReport().erred(), new FormDecoratorBuilder(10).build()));

        content.setWidget(++row, 0, 2, inject(proto().executionReport().message(), new FormDecoratorBuilder(true).build()));
        content.setWidget(++row, 0, 2, inject(proto().errorMessage(), new FormDecoratorBuilder(true).build()));

        reportSectionLister = new ExecutionReportSectionLister();
        content.setH4(++row, 0, 2, i18n.tr("Details"));
        content.setWidget(++row, 0, 2, reportSectionLister);

        content.setH2(++row, 0, 2, i18n.tr("Data"));
        content.setWidget(++row, 0, 2, ((RunViewerView) getParentView()).getRunDataListerView().asWidget());

        setTabBarVisible(false);
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