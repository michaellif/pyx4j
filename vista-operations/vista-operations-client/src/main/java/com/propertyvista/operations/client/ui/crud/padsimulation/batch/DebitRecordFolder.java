/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 7, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.padsimulation.batch;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.operations.domain.payment.pad.sim.PadSimDebitRecord;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;

public class DebitRecordFolder extends VistaBoxFolder<PadSimDebitRecord> {

    private static final I18n i18n = I18n.get(DebitRecordEditor.class);

    public DebitRecordFolder(boolean modifyable) {
        super(PadSimDebitRecord.class, modifyable);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof PadSimDebitRecord) {
            return new DebitRecordEditor();
        }
        return super.create(member);
    }

    private class DebitRecordEditor extends CEntityDecoratableForm<PadSimDebitRecord> {

        public DebitRecordEditor() {
            super(PadSimDebitRecord.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));

            int row = -1;
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().amount()), 10).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().clientId()), 10).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().transactionId()), 10).build());
            content.setHTML(++row, 0, "<hr style='border-top: 1px dashed #000000'/>");
            content.getFlexCellFormatter().setColSpan(row, 0, 2);
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().acknowledgmentStatusCode()), 10).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().reconciliationStatus()), 10).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().reasonCode()), 10).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().fee()), 10).build());

            row = -1;
            content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().bankId()), 10).build());
            content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().branchTransitNumber()), 10).build());
            content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().accountNumber()), 10).build());
            ++row;
            ++row;
            content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().paymentDate()), 10).build());
            content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().reasonText()), 10).build());

            return content;
        }
    }
}