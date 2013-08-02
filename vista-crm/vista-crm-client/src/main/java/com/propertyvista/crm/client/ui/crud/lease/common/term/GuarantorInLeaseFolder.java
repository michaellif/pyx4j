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
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CSimpleEntityComboBox;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;

public class GuarantorInLeaseFolder extends LeaseTermParticipantFolder<LeaseTermGuarantor> {

    static final I18n i18n = I18n.get(GuarantorInLeaseFolder.class);

    public GuarantorInLeaseFolder() {
        this(false);
    }

    public GuarantorInLeaseFolder(boolean modifiable) {
        super(LeaseTermGuarantor.class, modifiable);
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
            LeaseTermGuarantor newGuarantor = createGuarantor();
            newGuarantor.leaseParticipant().customer().set(customer);
            addItem(newGuarantor);
        }
    }

    @Override
    protected void addParticipant() {
        addItem(createGuarantor());
    }

    private LeaseTermGuarantor createGuarantor() {
        LeaseTermGuarantor guarantor = EntityFactory.create(LeaseTermGuarantor.class);

        guarantor.leaseTermV().setValueDetached();
        guarantor.role().setValue(LeaseTermParticipant.Role.Guarantor);
        guarantor.relationship().setValue(PersonRelationship.Other); // just not leave it empty - it's mandatory field!

        return guarantor;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof LeaseTermGuarantor) {
            return new GuarantorInLeaseEditor();
        }
        return super.create(member);
    }

    void updateTenantList() {
        for (CComponent<?> c : getComponents()) {
            @SuppressWarnings("rawtypes")
            CEntityFolderItem i = (CEntityFolderItem) c;
            ((GuarantorInLeaseEditor) i.getComponents().iterator().next()).updateTenantList();
        }
    }

    /**
     * override in order to supply current Tenants list
     * 
     * @return - current Tenants list
     */
    protected IList<LeaseTermTenant> getLeaseTermTenants() {
        return null;
    }

    private class GuarantorInLeaseEditor extends CEntityDecoratableForm<LeaseTermGuarantor> {

        public GuarantorInLeaseEditor() {
            super(LeaseTermGuarantor.class);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

            TwoColumnFlexFormPanel left = new TwoColumnFlexFormPanel();
            int row = -1;
            left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseParticipant().participantId()), 7).build());
            left.setWidget(++row, 0, inject(proto().leaseParticipant().customer().person().name(), new NameEditor(i18n.tr("Guarantor"), Guarantor.class) {
                @Override
                public Key getLinkKey() {
                    return GuarantorInLeaseEditor.this.getValue().leaseParticipant().getPrimaryKey();
                }
            }));
            left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseParticipant().customer().person().sex()), 7).build());
            left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseParticipant().customer().person().birthDate()), 9).build());
            if (isEditable()) {
                left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().tenant(), new CSimpleEntityComboBox<Tenant>()), 25).build());
            } else {
                left.setWidget(++row, 0,
                        new FormDecoratorBuilder(inject(proto().tenant(), new CEntityCrudHyperlink<Tenant>(AppPlaceEntityMapper.resolvePlace(Tenant.class))))
                                .build());
            }

            left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().relationship()), 15).build());
            left.setWidget(
                    ++row,
                    0,
                    new FormDecoratorBuilder(inject(proto().effectiveScreening(),
                            new CEntityCrudHyperlink<CustomerScreening>(AppPlaceEntityMapper.resolvePlace(CustomerScreening.class))), 9).build());

            TwoColumnFlexFormPanel right = new TwoColumnFlexFormPanel();
            row = -1;
            right.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseParticipant().customer().person().email()), 25).build());
            right.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseParticipant().customer().person().homePhone()), 15).build());
            right.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseParticipant().customer().person().mobilePhone()), 15).build());
            right.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseParticipant().customer().person().workPhone()), 15).build());

            // assemble main panel:
            main.setWidget(0, 0, left);
            main.setWidget(0, 1, right);

            return main;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            get(proto().effectiveScreening()).setVisible(!getValue().effectiveScreening().isNull());

            if (isEditable()) {
                ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.guarantor, get(proto().leaseParticipant().participantId()), getValue()
                        .getPrimaryKey());

                get(proto().leaseParticipant().customer().person().email()).setMandatory(!getValue().leaseParticipant().customer().user().isNull());

                updateTenantList();
            }
        }

        void updateTenantList() {
            if (get(proto().tenant()) instanceof CComboBox<?>) {
                CComboBox<Tenant> combo = (CComboBox<Tenant>) get(proto().tenant());
                combo.setOptions(getLeaseCustomerTenants());
            }
        }

        private List<Tenant> getLeaseCustomerTenants() {
            List<Tenant> l = new ArrayList<Tenant>();
            for (LeaseTermTenant t : getLeaseTermTenants()) {
                l.add(t.leaseParticipant());
            }
            return l;
        }

        @Override
        public void addValidations() {
            super.addValidations();

            get(proto().leaseParticipant().customer().person().birthDate()).addValueValidator(new EditableValueValidator<LogicalDate>() {
                @Override
                public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                    if (getAgeOfMajority() != null && !getValue().leaseParticipant().customer().person().birthDate().isNull()) {
                        if (!TimeUtils.isOlderThan(getValue().leaseParticipant().customer().person().birthDate().getValue(), getAgeOfMajority() - 1)) {
                            return new ValidationError(component, i18n.tr("The minimum age requirement for a guarantor is {0}.", getAgeOfMajority()));
                        }

                    }
                    return null;
                }
            });

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
            NameEditor nameEditor = (NameEditor) get(proto().leaseParticipant().customer().person().name());
            nameEditor.get(nameEditor.proto().firstName()).setValue("FirstnameG");
            nameEditor.get(nameEditor.proto().lastName()).setValue("LastnameG");
            get(proto().leaseParticipant().customer().person().birthDate()).setValue(new LogicalDate(80, 1, 1));
            get(proto().relationship()).setValue(PersonRelationship.Grandfather);
        }
    }
}