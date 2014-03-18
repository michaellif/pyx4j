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
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.DepositSlipCheckDetailsRecordDTO;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;

public class MoneyInBatchForm extends CrmEntityForm<MoneyInBatchDTO> {

    private static final I18n i18n = I18n.get(MoneyInBatchForm.class);

    public class DepositSlipPaymentRecordFolder extends VistaTableFolder<DepositSlipCheckDetailsRecordDTO> {

        private final List<EntityFolderColumnDescriptor> columns;

        public DepositSlipPaymentRecordFolder() {
            super(DepositSlipCheckDetailsRecordDTO.class);
            columns = Arrays.asList(//@formatter:off
                    new EntityFolderColumnDescriptor(proto().id(), "100px"),
                    new EntityFolderColumnDescriptor(proto().status(), "100px"),
                    new EntityFolderColumnDescriptor(proto().unit(), "50px"),
                    new EntityFolderColumnDescriptor(proto().tenantId(), "100px"),
                    new EntityFolderColumnDescriptor(proto().tenantName(), "250px"),
                    new EntityFolderColumnDescriptor(proto().checkNumber(), "150px"),
                    new EntityFolderColumnDescriptor(proto().amount(), "150px")
            );//@formatter:on

        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return columns;
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (DepositSlipCheckDetailsRecordDTO.class.equals(member.getObjectClass())) {
                return new CEntityFolderRowEditor<DepositSlipCheckDetailsRecordDTO>(DepositSlipCheckDetailsRecordDTO.class, columns(),
                        new VistaViewersComponentFactory()) {
                    @Override
                    public CComponent<?> create(IObject<?> member) {
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
            } else {
                return super.create(member);
            }
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
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().building())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().totalReceivedAmount())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().numberOfReceipts())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().postingStatus())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().batchNumber())).build());

        row = -1;
        panel.setWidget(++row, 1, 1, new FormDecoratorBuilder(inject(proto().depositSlipNumber())).build());
        panel.setWidget(++row, 1, 1, new FormDecoratorBuilder(inject(proto().depositDate())).build());
        panel.setWidget(++row, 1, 1, new FormDecoratorBuilder(inject(proto().bankAccountName())).build());
        panel.setWidget(++row, 1, 1, new FormDecoratorBuilder(inject(proto().bankId())).build());
        panel.setWidget(++row, 1, 1, new FormDecoratorBuilder(inject(proto().bankTransitNumber())).build());
        panel.setWidget(++row, 1, 1, new FormDecoratorBuilder(inject(proto().bankAccountNumber())).build());

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
