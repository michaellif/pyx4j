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
package com.propertyvista.operations.client.ui.crud.simulator.pad.batch;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.operations.domain.eft.caledoneft.simulator.PadSimDebitRecord;

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

    private class DebitRecordEditor extends CEntityForm<PadSimDebitRecord> {

        public DebitRecordEditor() {
            super(PadSimDebitRecord.class);
        }

        @Override
        protected IsWidget createContent() {
            TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));

            int row = -1;
            content.setWidget(++row, 0, inject(proto().amount(), new FormDecoratorBuilder(10).build()));
            content.setWidget(++row, 0, inject(proto().clientId(), new FormDecoratorBuilder(10).build()));
            content.setWidget(++row, 0, inject(proto().transactionId(), new FormDecoratorBuilder(10).build()));

            content.setHTML(++row, 0, "<hr style='border-top: 1px dashed #000000'/>");
            content.getFlexCellFormatter().setColSpan(row, 0, 2);
            content.setWidget(++row, 0, inject(proto().acknowledgmentStatusCode(), new FormDecoratorBuilder(10).build()));

            content.setHTML(++row, 0, "<hr style='border-top: 1px dashed #000000'/>");
            content.getFlexCellFormatter().setColSpan(row, 0, 2);

            content.setWidget(++row, 0, inject(proto().reconciliationStatus(), new FormDecoratorBuilder(10).build()));
            content.setWidget(++row, 0, inject(proto().reasonCode(), new FormDecoratorBuilder(10).build()));
            content.setWidget(++row, 0, inject(proto().fee(), new FormDecoratorBuilder(10).build()));

            row = -1;
            content.setWidget(++row, 1, inject(proto().bankId(), new FormDecoratorBuilder(10).build()));
            content.setWidget(++row, 1, inject(proto().branchTransitNumber(), new FormDecoratorBuilder(10).build()));
            content.setWidget(++row, 1, inject(proto().accountNumber(), new FormDecoratorBuilder(10).build()));
            ++row;
            ++row;
            ++row;
            content.setWidget(++row, 1, inject(proto().paymentDate(), new FormDecoratorBuilder(10).build()));
            content.setWidget(++row, 1, inject(proto().reasonText(), new FormDecoratorBuilder(10).build()));

            get(proto().acknowledgmentStatusCode()).addValueChangeHandler(new ValueChangeHandler<String>() {

                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    updateReconciliationEditablity();
                }

            });

            return content;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);
            updateReconciliationEditablity();
        }

        private void updateReconciliationEditablity() {
            boolean canEdit = getValue().acknowledgmentStatusCode().isNull();
            get(proto().reconciliationStatus()).setEnabled(canEdit);
            get(proto().paymentDate()).setEnabled(canEdit);
            get(proto().reasonCode()).setEnabled(canEdit);
            get(proto().reasonText()).setEnabled(canEdit);
            get(proto().fee()).setEnabled(canEdit);
        }
    }
}