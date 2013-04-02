/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 11, 2011
 * @author dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.portal.domain.dto.BillDataDTO;
import com.propertyvista.portal.domain.dto.BillingHistoryDTO;
import com.propertyvista.portal.domain.dto.PaymentDataDTO;

public class BillingHistoryForm extends CEntityForm<BillingHistoryDTO> implements BillingHistoryView {

    private static final I18n i18n = I18n.get(BillingHistoryForm.class);

    private BillingHistoryView.Presenter presenter;

    public BillingHistoryForm() {
        super(BillingHistoryDTO.class, new VistaViewersComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        content.setH1(++row, 0, 1, i18n.tr("Bills"));
        content.setWidget(++row, 0, inject(proto().bills(), new BillingHistoryFolder()));

        content.setH1(++row, 0, 1, i18n.tr("Payments"));
        content.setWidget(++row, 0, inject(proto().payments(), new PaymentHistoryFolder()));

        return content;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    private class BillingHistoryFolder extends VistaTableFolder<BillDataDTO> {

        public BillingHistoryFolder() {
            super(BillDataDTO.class, false);
            setOrderable(false);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
            columns.add(new EntityFolderColumnDescriptor(proto().referenceNo(), "5em"));
            columns.add(new EntityFolderColumnDescriptor(proto().amount(), "6em"));
            columns.add(new EntityFolderColumnDescriptor(proto().dueDate(), "5em"));
            columns.add(new EntityFolderColumnDescriptor(proto().fromDate(), "6em"));
            columns.add(new EntityFolderColumnDescriptor(proto().transactionId(), "10em"));
            columns.add(new EntityFolderColumnDescriptor(proto().transactionStatus(), "10em"));
            columns.add(new EntityFolderColumnDescriptor(proto().paymentMethod(), "5em"));
            return columns;
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof BillDataDTO) {
                return new BillEditor();
            }
            return super.create(member);
        }

        private class BillEditor extends CEntityFolderRowEditor<BillDataDTO> {

            public BillEditor() {
                super(BillDataDTO.class, columns());
            }

            @Override
            public CComponent<?, ?> create(IObject<?> member) {
                CComponent<?, ?> comp = null;
                if (member.equals(proto().amount())) {
                    comp = new CHyperlink<BigDecimal>(new Command() {
                        @Override
                        public void execute() {
                            presenter.view(getValue());
                        }
                    });
                    comp.setViewable(true);
                } else {
                    comp = super.create(member);
                }
                return comp;
            }
        }
    }

    private static class PaymentHistoryFolder extends VistaTableFolder<PaymentDataDTO> {

        private static final List<EntityFolderColumnDescriptor> COLUMNS;

        private static PaymentDataDTO proto;

        static {
            proto = EntityFactory.getEntityPrototype(PaymentDataDTO.class);
            COLUMNS = new ArrayList<EntityFolderColumnDescriptor>();
            COLUMNS.add(new EntityFolderColumnDescriptor(proto.paidOn(), "5em"));
            COLUMNS.add(new EntityFolderColumnDescriptor(proto.total(), "5em"));
            COLUMNS.add(new EntityFolderColumnDescriptor(proto.transactionId(), "5em"));
            COLUMNS.add(new EntityFolderColumnDescriptor(proto.status(), "5em"));
            COLUMNS.add(new EntityFolderColumnDescriptor(proto.paymentMethod(), "20em"));
        }

        public PaymentHistoryFolder() {
            super(PaymentDataDTO.class, false);
            setOrderable(false);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return COLUMNS;
        }

    }
}
