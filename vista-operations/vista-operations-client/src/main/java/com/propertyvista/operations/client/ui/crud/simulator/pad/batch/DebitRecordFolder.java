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
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.operations.domain.eft.caledoneft.simulator.PadSimDebitRecord;

public class DebitRecordFolder extends VistaBoxFolder<PadSimDebitRecord> {

    private static final I18n i18n = I18n.get(DebitRecordEditor.class);

    public DebitRecordFolder(boolean modifyable) {
        super(PadSimDebitRecord.class, modifyable);
    }

    @Override
    protected CForm<PadSimDebitRecord> createItemForm(IObject<?> member) {
        return new DebitRecordEditor();
    }

    private class DebitRecordEditor extends CForm<PadSimDebitRecord> {

        public DebitRecordEditor() {
            super(PadSimDebitRecord.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Left, proto().amount()).decorate().componentWidth(120);
            formPanel.append(Location.Left, proto().clientId()).decorate().componentWidth(120);
            formPanel.append(Location.Left, proto().transactionId()).decorate().componentWidth(120);

            formPanel.append(Location.Right, proto().bankId()).decorate().componentWidth(120);
            formPanel.append(Location.Right, proto().branchTransitNumber()).decorate().componentWidth(120);
            formPanel.append(Location.Right, proto().accountNumber()).decorate().componentWidth(120);

            formPanel.append(Location.Dual, proto().acknowledgmentStatusCode()).decorate().componentWidth(120);

            formPanel.append(Location.Dual, proto().reconciliationStatus()).decorate().componentWidth(120);
            formPanel.append(Location.Dual, proto().reasonCode()).decorate().componentWidth(120);

            formPanel.append(Location.Left, proto().fee()).decorate().componentWidth(120);
            formPanel.append(Location.Right, proto().paymentDate()).decorate().componentWidth(120);
            formPanel.append(Location.Right, proto().reasonText()).decorate().componentWidth(120);

            get(proto().acknowledgmentStatusCode()).addValueChangeHandler(new ValueChangeHandler<String>() {

                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    updateReconciliationEditablity();
                }

            });

            return formPanel;
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