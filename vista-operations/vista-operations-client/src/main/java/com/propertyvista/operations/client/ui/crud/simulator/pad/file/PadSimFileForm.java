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
package com.propertyvista.operations.client.ui.crud.simulator.pad.file;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCrudHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.caledoneft.simulator.PadSimFile;

public class PadSimFileForm extends OperationsEntityForm<PadSimFile> {

    private static final I18n i18n = I18n.get(PadSimFileForm.class);

    public PadSimFileForm(IFormView<PadSimFile> view) {
        super(PadSimFile.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("File Details"));
        formPanel.append(Location.Dual, createDetailsTab());

        if (!isEditable()) {
            formPanel.h1(i18n.tr("Batches"));
            formPanel.append(Location.Dual, ((PadSimFileViewerView) getParentView()).getBatchListerView().asWidget());
        }
        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
    }

    private IsWidget createDetailsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().fileName()).decorate();

        formPanel.append(Location.Left, proto().fundsTransferType()).decorate();
        formPanel.append(Location.Left, proto().state()).decorate();
        formPanel.append(Location.Left, proto().acknowledgmentStatusCode()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().recordsCount()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().batchRecordsCount()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().returnSent()).decorate().componentWidth(120);

        formPanel.append(Location.Right, proto().fileCreationNumber()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().originalFile(), new CEntityCrudHyperlink<PadSimFile>(AppPlaceEntityMapper.resolvePlace(PadSimFile.class)))
                .decorate();
        formPanel.append(Location.Right, proto().received()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().acknowledged()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().reconciliationSent()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().fileAmount()).decorate().componentWidth(120);

        formPanel.append(Location.Dual, proto().acknowledgmentRejectReasonMessage()).decorate();

        return formPanel;
    }

    @Override
    protected void onValuePropagation(PadSimFile value, boolean fireEvent, boolean populate) {
        super.onValuePropagation(value, fireEvent, populate);

        boolean returns = ((value != null) && (value.returns().getValue(Boolean.FALSE)));

        get(proto().originalFile()).setVisible(returns);
        get(proto().returnSent()).setVisible(returns);
    }
}