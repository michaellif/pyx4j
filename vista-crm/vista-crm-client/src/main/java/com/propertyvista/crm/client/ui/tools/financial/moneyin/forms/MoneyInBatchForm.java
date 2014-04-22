/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.financial.moneyin.forms;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.DepositSlipCheckDetailsRecordDTO;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;

public class MoneyInBatchForm extends CrmEntityForm<MoneyInBatchDTO> {

    private static final I18n i18n = I18n.get(MoneyInBatchForm.class);

    public class DepositSlipPaymentRecordFolder extends VistaTableFolder<DepositSlipCheckDetailsRecordDTO> {

        private final List<FolderColumnDescriptor> columns;

        public DepositSlipPaymentRecordFolder() {
            super(DepositSlipCheckDetailsRecordDTO.class);
            columns = Arrays.asList(//@formatter:off
                    new FolderColumnDescriptor(proto().id(), "100px"),
                    new FolderColumnDescriptor(proto().status(), "100px"),
                    new FolderColumnDescriptor(proto().unit(), "50px"),
                    new FolderColumnDescriptor(proto().tenantId(), "100px"),
                    new FolderColumnDescriptor(proto().tenantName(), "250px"),
                    new FolderColumnDescriptor(proto().checkNumber(), "150px"),
                    new FolderColumnDescriptor(proto().amount(), "150px")
            );//@formatter:on

        }

        @Override
        public List<FolderColumnDescriptor> columns() {
            return columns;
        }

        @Override
        protected CForm<DepositSlipCheckDetailsRecordDTO> createItemForm(IObject<?> member) {
            return new CFolderRowEditor<DepositSlipCheckDetailsRecordDTO>(DepositSlipCheckDetailsRecordDTO.class, columns(),
                    new VistaViewersComponentFactory()) {
                @Override
                public CField<?, ?> create(IObject<?> member) {
                    if (proto().id().getFieldName().equals(member.getFieldName())) {
                        CLabel<Key> idLabel = new CLabel<Key>();
                        idLabel.setNavigationCommand(new Command() {
                            @Override
                            public void execute() {
                                onShowToPaymentRecord(getValue().id().getValue());
                            }
                        });
                        return idLabel;
                    } else {
                        return super.create(member);
                    }
                }
            };
        }

    }

    public void onShowToPaymentRecord(Key paymentRecordId) {

    }

    public MoneyInBatchForm(IForm<MoneyInBatchDTO> view) {
        super(MoneyInBatchDTO.class, view);
        selectTab(addTab(createGeneralTab(), i18n.tr("Batch Details")));
    }

    private Widget createGeneralTab() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;
        panel.setWidget(++row, 0, 1, inject(proto().building(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().totalReceivedAmount(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().numberOfReceipts(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().postingStatus(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().batchNumber(), new FieldDecoratorBuilder().build()));

        row = -1;
        panel.setWidget(++row, 1, 1, inject(proto().depositSlipNumber(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 1, 1, inject(proto().depositDate(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 1, 1, inject(proto().bankAccountName(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 1, 1, inject(proto().bankId(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 1, 1, inject(proto().bankTransitNumber(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 1, 1, inject(proto().bankAccountNumber(), new FieldDecoratorBuilder().build()));

        ++row;
        panel.setH2(++row, 0, 2, i18n.tr("Payments"));
        panel.setWidget(++row, 0, 2, inject(proto().payments(), new DepositSlipPaymentRecordFolder()));

        get(proto().building()).setViewable(true);
        get(proto().depositSlipNumber()).setViewable(true);
        get(proto().totalReceivedAmount()).setViewable(true);
        get(proto().numberOfReceipts()).setViewable(true);
        get(proto().postingStatus()).setViewable(true);
        get(proto().payments()).setViewable(true);

        get(proto().bankId()).setViewable(true);
        get(proto().bankTransitNumber()).setViewable(true);
        get(proto().bankAccountNumber()).setViewable(true);
        get(proto().bankAccountName()).setViewable(true);

        return panel;
    }

}
