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

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCrudHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.client.ui.components.OperationsEditorsComponentFactory;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.scheduler.ExecutionReport;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.domain.scheduler.RunData;
import com.propertyvista.operations.rpc.dto.PmcDTO;
import com.propertyvista.operations.rpc.dto.TriggerDTO;

public class RunDataForm extends OperationsEntityForm<RunData> {

    private static final I18n i18n = I18n.get(RunDataForm.class);

    private final ExecutionReportSectionLister reportSectionLister;

    public RunDataForm(IFormView<RunData, ?> view) {
        super(RunData.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().execution().trigger(), OperationsEditorsComponentFactory.createEntityHyperlink(TriggerDTO.class)).decorate();

        formPanel.append(Location.Right, proto().execution(), OperationsEditorsComponentFactory.createEntityHyperlink(Run.class)).decorate();

        formPanel.append(Location.Left, proto().pmc(), new CEntityCrudHyperlink<Pmc>(AppPlaceEntityMapper.resolvePlace(PmcDTO.class))).decorate();
        formPanel.append(Location.Right, proto().started()).decorate();

        formPanel.append(Location.Left, proto().status()).decorate();

        formPanel.h2(i18n.tr("Statistics"));

        formPanel.append(Location.Left, proto().executionReport().total()).decorate();
        formPanel.append(Location.Right, proto().executionReport().averageDuration()).decorate();
        formPanel.append(Location.Left, proto().executionReport().processed()).decorate();
        formPanel.append(Location.Right, proto().executionReport().totalDuration()).decorate();
        formPanel.append(Location.Left, proto().executionReport().failed()).decorate();
        formPanel.append(Location.Left, proto().executionReport().erred()).decorate();
        formPanel.append(Location.Left, proto().executionReport().detailsErred()).decorate();

        formPanel.append(Location.Dual, proto().executionReport().message()).decorate();
        formPanel.append(Location.Dual, proto().errorMessage()).decorate();

        reportSectionLister = new ExecutionReportSectionLister();
        formPanel.h4(i18n.tr("Details"));
        formPanel.append(Location.Dual, reportSectionLister);

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