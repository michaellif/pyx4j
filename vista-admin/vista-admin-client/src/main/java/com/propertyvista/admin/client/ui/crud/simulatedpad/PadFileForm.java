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
package com.propertyvista.admin.client.ui.crud.simulatedpad;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.domain.payment.pad.sim.PadSimFile;

public class PadFileForm extends AdminEntityForm<PadSimFile> {

    private static final I18n i18n = I18n.get(PadFileForm.class);

    public PadFileForm() {
        this(false);
    }

    public PadFileForm(boolean viewMode) {
        super(PadSimFile.class, viewMode);
    }

    @Override
    public void createTabs() {
        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));
        int row = -1;

        content.setH1(++row, 0, 1, i18n.tr("File Details"));
        content.setWidget(++row, 0, createDetailsTab());

        if (!isEditable()) {
            content.setH1(++row, 0, 1, i18n.tr("Batches"));
            content.setWidget(++row, 0, ((PadFileViewerView) getParentView()).getBatchListerView().asWidget());
        }
        selectTab(addTab(content));
    }

    private Widget createDetailsTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().fileName()), 20).build());
        main.setWidget(row, 1, new DecoratorBuilder(inject(proto().fileCreationNumber()), 10).build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status()), 10).build());
        main.setWidget(row, 1, new DecoratorBuilder(inject(proto().acknowledgmentStatusCode()), 10).build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().received()), 10).build());
        main.setWidget(row, 1, new DecoratorBuilder(inject(proto().acknowledgmentRejectReasonMessage()), 40).build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().acknowledged()), 10).build());
        main.setWidget(row, 1, new DecoratorBuilder(inject(proto().reconciliationSent()), 10).build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().batchRecordsCount()), 10).build());
        main.setWidget(row, 1, new DecoratorBuilder(inject(proto().recordsCount()), 10).build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().fileAmount()), 10).build());

        return main;
    }
}