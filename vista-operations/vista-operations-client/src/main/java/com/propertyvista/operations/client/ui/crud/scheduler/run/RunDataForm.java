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

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.scheduler.ExecutionReport;
import com.propertyvista.operations.domain.scheduler.RunData;
import com.propertyvista.operations.rpc.PmcDTO;

public class RunDataForm extends OperationsEntityForm<RunData> {

    private static final I18n i18n = I18n.get(RunDataForm.class);

    private final ExecutionReportSectionLister reportSectionLister;

    public RunDataForm(IForm<RunData> view) {
        super(RunData.class, view);

        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));
        int row = -1;

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().pmc(), new CEntityCrudHyperlink<Pmc>(AppPlaceEntityMapper.resolvePlace(PmcDTO.class))),
                10).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().started()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status()), 15).build());

        content.setH2(++row, 0, 2, i18n.tr("Statistics"));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().executionReport().total()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().executionReport().processed()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().executionReport().failed()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().executionReport().erred()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().executionReport().averageDuration()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().executionReport().totalDuration()), 10).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().executionReport().message()), 40).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().errorMessage()), 40).build());

        reportSectionLister = new ExecutionReportSectionLister();
        content.setH4(++row, 0, 2, i18n.tr("Details"));
        content.setWidget(++row, 0, reportSectionLister);

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