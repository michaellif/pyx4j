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

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.DepositSlipPaymentRecordDTO;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;

public class MoneyInBatchForm extends CrmEntityForm<MoneyInBatchDTO> {

    private static final I18n i18n = I18n.get(MoneyInBatchForm.class);

    public static class DepositSlipPaymentRecordFolder extends VistaTableFolder<DepositSlipPaymentRecordDTO> {

        private final List<EntityFolderColumnDescriptor> columns;

        public DepositSlipPaymentRecordFolder() {
            super(DepositSlipPaymentRecordDTO.class);
            columns = Arrays.asList(//@formatter:off
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

    }

    public MoneyInBatchForm(IForm<MoneyInBatchDTO> view) {
        super(MoneyInBatchDTO.class, view);
        selectTab(addTab(createGeneralTab(), i18n.tr("Batch Details")));
    }

    private Widget createGeneralTab() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().building())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().bankAccount())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().bankDepositDate())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().depositSlipNumber())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().totalReceivedAmount())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().numberOfReceipts())).build());
        panel.setWidget(++row, 0, 1, new FormDecoratorBuilder(inject(proto().isPosted())).build());
        panel.setH2(++row, 0, 2, i18n.tr("Payments"));
        panel.setWidget(++row, 0, 2, inject(proto().payments(), new DepositSlipPaymentRecordFolder()));
        return panel;
    }

}
