/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant.lease;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.components.boxes.CustomerSelectorDialog;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.lease.Lease;

class GuarantorInLeaseFolder extends VistaBoxFolder<Guarantor> {

    static final I18n i18n = I18n.get(GuarantorInLeaseFolder.class);

    private final CEntityEditor<? extends Lease> parent;

    public GuarantorInLeaseFolder(CEntityEditor<? extends Lease> parent, boolean modifiable) {
        super(Guarantor.class, modifiable);
        this.parent = parent;
        setOrderable(false);
    }

    @Override
    protected void addItem() {
        new CustomerSelectorDialog(retrieveExistingCustomers(getValue())) {
            @Override
            public boolean onClickOk() {
                if (getSelectedItems().isEmpty()) {
                    return false;
                } else {
                    for (Customer tenant : getSelectedItems()) {
                        Guarantor newTenantInLease = EntityFactory.create(Guarantor.class);
                        newTenantInLease.leaseV().setPrimaryKey(parent.getValue().version().getPrimaryKey());
                        newTenantInLease.customer().set(tenant);
                        newTenantInLease.relationship().setValue(PersonRelationship.Other); // just not leave it empty - it's mandatory field!
                        addItem(newTenantInLease);
                    }
                    return true;
                }
            }
        }.show();
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof Guarantor) {
            return new GuarantorInLeaseEditor();
        }
        return super.create(member);
    }

    private class GuarantorInLeaseEditor extends CEntityDecoratableEditor<Guarantor> {

        public GuarantorInLeaseEditor() {
            super(Guarantor.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            int row = -1;

            main.setWidget(++row, 0, inject(proto().customer().person().name(), new NameEditor(i18n.tr("Guarantor"))));
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().sex()), 7).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().birthDate()), 9).build());

            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().relationship()), 15).build());

            row = -1; // second column
            main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().customer().person().email()), 25).build());
            main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().customer().person().homePhone()), 15).build());
            main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().customer().person().mobilePhone()), 15).build());
            main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().customer().person().workPhone()), 15).build());

            main.getColumnFormatter().setWidth(0, "60%");
            main.getColumnFormatter().setWidth(1, "40%");
            return main;
        }
    }

    private static List<Customer> retrieveExistingCustomers(List<Guarantor> list) {
        List<Customer> tenants = new ArrayList<Customer>(list.size());
        for (Guarantor wrapper : list) {
            tenants.add(wrapper.customer());
        }
        return tenants;
    }
}