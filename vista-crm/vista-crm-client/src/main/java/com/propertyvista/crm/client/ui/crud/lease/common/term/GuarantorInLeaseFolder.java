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
package com.propertyvista.crm.client.ui.crud.lease.common.term;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CSimpleEntityComboBox;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.LeaseCustomerGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseCustomerTenant;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.LeaseTermDTO;

public class GuarantorInLeaseFolder extends LeaseParticipantFolder<Guarantor> {

    static final I18n i18n = I18n.get(GuarantorInLeaseFolder.class);

    private final CEntityForm<LeaseTermDTO> leaseTerm;

    public GuarantorInLeaseFolder(CEntityForm<LeaseTermDTO> parent, boolean modifiable) {
        super(Guarantor.class, modifiable);
        this.leaseTerm = parent;
    }

    @Override
    protected String getAddItemDialogCaption() {
        return i18n.tr("Add New Guarantor");
    }

    @Override
    protected String getAddItemDialogBody() {
        return i18n.tr("Do you want to select existing Guarantor?");
    }

    @Override
    protected void addParticipants(List<Customer> customers) {
        for (Customer customer : customers) {
            Guarantor newGuarantor = createGuarantor();
            newGuarantor.leaseCustomer().customer().set(customer);
            addItem(newGuarantor);
        }
    }

    @Override
    protected void addParticipant() {
        addItem(createGuarantor());
    }

    private Guarantor createGuarantor() {
        Guarantor guarantor = EntityFactory.create(Guarantor.class);

        guarantor.leaseTermV().setPrimaryKey(leaseTerm.getValue().version().getPrimaryKey());
        guarantor.role().setValue(LeaseParticipant.Role.Guarantor);
        guarantor.relationship().setValue(PersonRelationship.Other); // just not leave it empty - it's mandatory field!

        return guarantor;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof Guarantor) {
            return new GuarantorInLeaseEditor();
        }
        return super.create(member);
    }

    void updateTenantList() {
        for (CComponent<?, ?> c : getComponents()) {
            @SuppressWarnings("rawtypes")
            CEntityFolderItem i = (CEntityFolderItem) c;
            ((GuarantorInLeaseEditor) i.getComponents().iterator().next()).updateTenantList();
        }
    }

    private class GuarantorInLeaseEditor extends CEntityDecoratableForm<Guarantor> {

        public GuarantorInLeaseEditor() {
            super(Guarantor.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            FormFlexPanel left = new FormFlexPanel();
            int row = -1;
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseCustomer().participantId()), 7).build());
            left.setWidget(++row, 0,
                    inject(proto().leaseCustomer().customer().person().name(), new NameEditor(i18n.tr("Guarantor"), LeaseCustomerGuarantor.class) {
                        @Override
                        public Key getLinkKey() {
                            return GuarantorInLeaseEditor.this.getValue().leaseCustomer().getPrimaryKey();
                        }
                    }));
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseCustomer().customer().person().sex()), 7).build());
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseCustomer().customer().person().birthDate()), 9).build());
            if (isEditable()) {
                left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().tenant(), new CSimpleEntityComboBox<LeaseCustomerTenant>()), 25).build());
            } else {
                left.setWidget(
                        ++row,
                        0,
                        new DecoratorBuilder(inject(proto().tenant(),
                                new CEntityCrudHyperlink<LeaseCustomerTenant>(AppPlaceEntityMapper.resolvePlace(LeaseCustomerTenant.class)))).build());
            }

            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().relationship()), 15).build());
            left.setWidget(
                    ++row,
                    0,
                    new DecoratorBuilder(inject(proto().effectiveScreening(),
                            new CEntityCrudHyperlink<CustomerScreening>(AppPlaceEntityMapper.resolvePlace(CustomerScreening.class))), 9).build());

            FormFlexPanel right = new FormFlexPanel();
            row = -1;
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseCustomer().customer().person().email()), 25).build());
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseCustomer().customer().person().homePhone()), 15).build());
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseCustomer().customer().person().mobilePhone()), 15).build());
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseCustomer().customer().person().workPhone()), 15).build());

            // assemble main panel:
            main.setWidget(0, 0, left);
            main.setWidget(0, 1, right);

            main.getColumnFormatter().setWidth(0, "50%");
            main.getColumnFormatter().setWidth(1, "50%");

            return main;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            get(proto().effectiveScreening()).setVisible(!getValue().effectiveScreening().isNull());

            if (isEditable()) {
                ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.guarantor, get(proto().leaseCustomer().participantId()), getValue()
                        .getPrimaryKey());

                get(proto().leaseCustomer().customer().person().email()).setMandatory(!getValue().leaseCustomer().customer().user().isNull());

                updateTenantList();
            }
        }

        void updateTenantList() {
            if (get(proto().tenant()) instanceof CComboBox<?>) {
                @SuppressWarnings("unchecked")
                CComboBox<LeaseCustomerTenant> combo = (CComboBox<LeaseCustomerTenant>) get(proto().tenant());
                combo.setOptions(getLeaseCustomerTenants());
            }
        }

        private List<LeaseCustomerTenant> getLeaseCustomerTenants() {
            List<LeaseCustomerTenant> l = new ArrayList<LeaseCustomerTenant>();
            for (Tenant t : leaseTerm.getValue().version().tenants()) {
                l.add(t.leaseCustomer());
            }
            return l;
        }

        @Override
        public void addValidations() {
            super.addValidations();
            if (ApplicationMode.isDevelopment()) {
                this.addDevShortcutHandler(new DevShortcutHandler() {
                    @Override
                    public void onDevShortcut(DevShortcutEvent event) {
                        if (event.getKeyCode() == 'Q') {
                            event.consume();
                            devGenerateTenant();
                        }
                    }
                });
            }
        }

        private void devGenerateTenant() {
            NameEditor nameEditor = (NameEditor) get(proto().leaseCustomer().customer().person().name());
            nameEditor.get(nameEditor.proto().firstName()).setValue("FirstnameG");
            nameEditor.get(nameEditor.proto().lastName()).setValue("LastnameG");
            get(proto().leaseCustomer().customer().person().birthDate()).setValue(new LogicalDate(80, 1, 1));
            get(proto().relationship()).setValue(PersonRelationship.Grandfather);
        }
    }
}