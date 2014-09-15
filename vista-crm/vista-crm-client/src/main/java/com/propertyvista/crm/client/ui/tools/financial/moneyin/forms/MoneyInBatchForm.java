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
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IForm;

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
            return new CFolderRowEditor<DepositSlipCheckDetailsRecordDTO>(DepositSlipCheckDetailsRecordDTO.class, columns(), new VistaViewersComponentFactory()) {
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
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().building()).decorate();
        formPanel.append(Location.Left, proto().totalReceivedAmount()).decorate();
        formPanel.append(Location.Left, proto().numberOfReceipts()).decorate();
        formPanel.append(Location.Left, proto().postingStatus()).decorate();
        formPanel.append(Location.Left, proto().batchNumber()).decorate();

        formPanel.append(Location.Right, proto().depositSlipNumber()).decorate();
        formPanel.append(Location.Right, proto().depositDate()).decorate();
        formPanel.append(Location.Right, proto().bankAccountName()).decorate();
        formPanel.append(Location.Right, proto().bankId()).decorate();
        formPanel.append(Location.Right, proto().bankTransitNumber()).decorate();
        formPanel.append(Location.Right, proto().bankAccountNumber()).decorate();

        formPanel.h2(i18n.tr("Payments"));
        formPanel.append(Location.Dual, proto().payments(), new DepositSlipPaymentRecordFolder());

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

        return formPanel.asWidget();
    }

}
