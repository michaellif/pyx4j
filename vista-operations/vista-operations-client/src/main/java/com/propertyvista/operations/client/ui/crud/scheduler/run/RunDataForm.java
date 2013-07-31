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
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.scheduler.run;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.client.ui.components.OperationsEditorsComponentFactory;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.scheduler.ExecutionReport;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.domain.scheduler.RunData;
import com.propertyvista.operations.rpc.PmcDTO;
import com.propertyvista.operations.rpc.TriggerDTO;

public class RunDataForm extends OperationsEntityForm<RunData> {

    private static final I18n i18n = I18n.get(RunDataForm.class);

    private final ExecutionReportSectionLister reportSectionLister;

    public RunDataForm(IForm<RunData> view) {
        super(RunData.class, view);

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0,
                new FormDecoratorBuilder(inject(proto().execution().trigger(), OperationsEditorsComponentFactory.createEntityHyperlink(TriggerDTO.class)))
                        .build());

        content.setWidget(row, 1,
                new FormDecoratorBuilder(inject(proto().execution(), OperationsEditorsComponentFactory.createEntityHyperlink(Run.class))).build());

        content.setWidget(++row, 0,
                new FormDecoratorBuilder(inject(proto().pmc(), new CEntityCrudHyperlink<Pmc>(AppPlaceEntityMapper.resolvePlace(PmcDTO.class)))).build());
        content.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().started())).build());

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().status())).build());

        content.setH2(++row, 0, 2, i18n.tr("Statistics"));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().executionReport().total())).build());
        content.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().executionReport().averageDuration())).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().executionReport().processed())).build());
        content.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().executionReport().totalDuration())).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().executionReport().failed())).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().executionReport().erred())).build());

        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().executionReport().message()), true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().errorMessage()), true).build());

        reportSectionLister = new ExecutionReportSectionLister();
        content.setH4(++row, 0, 2, i18n.tr("Details"));
        content.setWidget(++row, 0, 2, reportSectionLister);

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