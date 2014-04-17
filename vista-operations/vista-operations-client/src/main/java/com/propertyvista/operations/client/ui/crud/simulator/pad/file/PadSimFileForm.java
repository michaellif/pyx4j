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

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.caledoneft.simulator.PadSimFile;

public class PadSimFileForm extends OperationsEntityForm<PadSimFile> {

    private static final I18n i18n = I18n.get(PadSimFileForm.class);

    public PadSimFileForm(IForm<PadSimFile> view) {
        super(PadSimFile.class, view);

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));
        int row = -1;

        content.setH1(++row, 0, 1, i18n.tr("File Details"));
        content.setWidget(++row, 0, createDetailsTab());

        if (!isEditable()) {
            content.setH1(++row, 0, 1, i18n.tr("Batches"));
            content.setWidget(++row, 0, ((PadSimFileViewerView) getParentView()).getBatchListerView().asWidget());
        }
        selectTab(addTab(content));
    }

    private Widget createDetailsTab() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        int row = -1;
        main.setWidget(++row, 0, inject(proto().fileName(), new FieldDecoratorBuilder(25).build()));
        main.setWidget(row, 1, inject(proto().fileCreationNumber(), new FieldDecoratorBuilder(10).build()));

        main.setWidget(++row, 0, inject(proto().fundsTransferType(), new FieldDecoratorBuilder(25).build()));

        main.setWidget(
                ++row,
                0,
                inject(proto().originalFile(), new CEntityCrudHyperlink<PadSimFile>(AppPlaceEntityMapper.resolvePlace(PadSimFile.class)),
                        new FieldDecoratorBuilder(35).build()));
        main.setWidget(++row, 0, inject(proto().returnSent(), new FieldDecoratorBuilder(10).build()));

        main.setWidget(++row, 0, inject(proto().state(), new FieldDecoratorBuilder(25).build()));
        main.setWidget(row, 1, inject(proto().acknowledgmentStatusCode(), new FieldDecoratorBuilder(10).build()));

        main.setWidget(++row, 0, inject(proto().received(), new FieldDecoratorBuilder(10).build()));
        main.setWidget(row, 1, inject(proto().acknowledgmentRejectReasonMessage(), new FieldDecoratorBuilder(40).build()));

        main.setWidget(++row, 0, inject(proto().acknowledged(), new FieldDecoratorBuilder(10).build()));
        main.setWidget(row, 1, inject(proto().reconciliationSent(), new FieldDecoratorBuilder(10).build()));

        main.setWidget(++row, 0, inject(proto().batchRecordsCount(), new FieldDecoratorBuilder(10).build()));
        main.setWidget(row, 1, inject(proto().recordsCount(), new FieldDecoratorBuilder(10).build()));

        main.setWidget(++row, 0, inject(proto().fileAmount(), new FieldDecoratorBuilder(10).build()));

        return main;
    }

    @Override
    protected void onValuePropagation(PadSimFile value, boolean fireEvent, boolean populate) {
        super.onValuePropagation(value, fireEvent, populate);

        boolean returns = ((value != null) && (value.returns().getValue(Boolean.FALSE)));

        get(proto().originalFile()).setVisible(returns);
        get(proto().returnSent()).setVisible(returns);
    }
}