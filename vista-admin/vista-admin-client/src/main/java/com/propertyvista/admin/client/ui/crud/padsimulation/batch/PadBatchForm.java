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
package com.propertyvista.admin.client.ui.crud.padsimulation.batch;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;

import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.domain.payment.pad.sim.PadSimBatch;
import com.propertyvista.admin.domain.payment.pad.sim.PadSimFile;

public class PadBatchForm extends AdminEntityForm<PadSimBatch> {

    private static final I18n i18n = I18n.get(PadBatchForm.class);

    public PadBatchForm() {
        this(false);
    }

    public PadBatchForm(boolean viewMode) {
        super(PadSimBatch.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;

        main.setH1(++row, 0, 1, i18n.tr("Batch Details"));

        main.setWidget(++row, 0,
                new DecoratorBuilder(inject(proto().padFile(), new CEntityCrudHyperlink<PadSimFile>(AppPlaceEntityMapper.resolvePlace(PadSimFile.class))), 10)
                        .build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().batchNumber()), 10).build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().terminalId()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().bankId()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().branchTransitNumber()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().accountNumber()), 10).build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().chargeDescription()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().recordsCount()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().batchAmount()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().acknowledgmentStatusCode()), 10).build());

        main.setH1(++row, 0, 1, i18n.tr("Detail Debit Records"));

        main.setWidget(++row, 0, inject(proto().records(), new DebitRecordFolder(isEditable())));

        return new ScrollPanel(main);
    }
}