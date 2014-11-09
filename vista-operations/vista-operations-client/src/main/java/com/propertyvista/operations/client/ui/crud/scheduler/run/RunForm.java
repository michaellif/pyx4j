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

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IForm;

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

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().trigger(), OperationsEditorsComponentFactory.createEntityHyperlink(TriggerDTO.class)).decorate();

        formPanel.append(Location.Right, proto().created()).decorate();
        formPanel.append(Location.Left, proto().trigger().triggerType()).decorate();
        formPanel.append(Location.Right, proto().forDate()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().startedBy()).decorate();
        formPanel.append(Location.Right, proto().started()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().status()).decorate().componentWidth(180);
        formPanel.append(Location.Right, proto().completed()).decorate().componentWidth(120);

        formPanel.h2(i18n.tr("Statistics"));
        //content.getFlexCellFormatter().setColSpan(row, 0, 2);
        formPanel.append(Location.Left, proto().executionReport().total()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().executionReport().averageDuration()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().executionReport().processed()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().executionReport().totalDuration()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().executionReport().failed()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().executionReport().erred()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().executionReport().detailsErred()).decorate();

        formPanel.append(Location.Dual, proto().executionReport().message()).decorate();
        formPanel.append(Location.Dual, proto().errorMessage()).decorate();

        reportSectionLister = new ExecutionReportSectionLister();
        formPanel.h4(i18n.tr("Details"));
        formPanel.append(Location.Dual, reportSectionLister);

        formPanel.h2(i18n.tr("Data"));
        formPanel.append(Location.Dual, ((RunViewerView) getParentView()).getRunDataListerView().asWidget());

        setTabBarVisible(false);
        selectTab(addTab(formPanel, i18n.tr("General")));

    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().errorMessage()).setVisible(!getValue().errorMessage().isNull());

        reportSectionLister.getDataSource().setParentEntityId(getValue().executionReport().getPrimaryKey(), ExecutionReport.class);
    }
}