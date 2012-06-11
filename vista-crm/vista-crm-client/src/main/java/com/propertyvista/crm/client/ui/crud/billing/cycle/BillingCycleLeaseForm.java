/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.billing.cycle;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.billing.BillingCycleLeaseDTO;
import com.propertyvista.domain.financial.billing.Bill;

public class BillingCycleLeaseForm extends CrmEntityForm<BillingCycleLeaseDTO> {

    private static final I18n i18n = I18n.get(BillingCycleLeaseForm.class);

    public BillingCycleLeaseForm() {
        super(BillingCycleLeaseDTO.class, true);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();
        int row = -1;

        main.setH2(++row, 0, 1, i18n.tr("Statistics"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().nonConfirmedBills())).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().nonRunnedBills())).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().failedBills())).build());

        main.setH2(++row, 0, 1, i18n.tr("Bills"));
        main.setWidget(++row, 0, inject(proto().bills(), new BillFolder()));

        return new ScrollPanel(main);
    }

    private class BillFolder extends VistaTableFolder<Bill> {

        public BillFolder() {
            super(Bill.class, false);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return Arrays.asList(//@formatter:off
                    new EntityFolderColumnDescriptor(proto().totalDueAmount(), "10em"),
                    new EntityFolderColumnDescriptor(proto().dueDate(), "10em"),
                    new EntityFolderColumnDescriptor(proto().executionDate(), "10em"),
                    new EntityFolderColumnDescriptor(proto().billStatus(), "10em")
                    ); //@formatter:on
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof Bill) {
                return new BillEditor();
            }
            return super.create(member);
        }

        private class BillEditor extends CEntityFolderRowEditor<Bill> {

            public BillEditor() {
                super(Bill.class, columns());
            }

            @Override
            public CComponent<?, ?> create(IObject<?> member) {
                CComponent<?, ?> comp = null;
                if (member.equals(proto().billStatus())) {
                    comp = new CHyperlink(new Command() {
                        @Override
                        public void execute() {
                            ((BillingCycleLeaseView.Presenter) ((BillingCycleLeaseView) getParentView()).getPresenter()).viewBill(getValue());
                        }
                    });
                } else {
                    comp = super.create(member);
                }
                return comp;
            }
        }
    }

}
