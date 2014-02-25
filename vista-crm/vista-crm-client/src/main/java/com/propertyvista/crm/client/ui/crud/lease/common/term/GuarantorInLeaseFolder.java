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
import java.util.Collections;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CSimpleEntityComboBox;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.LeaseParticipantScreeningTO;

public class GuarantorInLeaseFolder extends LeaseTermParticipantFolder<LeaseTermGuarantor> {

    static final I18n i18n = I18n.get(GuarantorInLeaseFolder.class);

    public GuarantorInLeaseFolder(CrmEntityForm<?> parentForm) {
        super(LeaseTermGuarantor.class, parentForm);
    }

    @Override
    public void setEnforceAgeOfMajority(boolean enforceAgeOfMajority) {
        super.setEnforceAgeOfMajority(enforceAgeOfMajority);

        for (CComponent<?> comp : getComponents()) {
            ((GuarantorInLeaseEditor) ((CEntityFolderItem<?>) comp).getComponents().iterator().next()).setEnforceAgeOfMajority(enforceAgeOfMajority);
        }
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
        for (CComponent<?> comp : getComponents()) {
            ((GuarantorInLeaseEditor) ((CEntityFolderItem<?>) comp).getComponents().iterator().next()).updateTenantList();
        }
    }

    /**
     * override in order to supply current Tenants list
     * 
     * @return - current Tenants list
     */
    protected List<LeaseTermTenant> getLeaseTermTenants() {
        return Collections.emptyList();
    }

    private class GuarantorInLeaseEditor extends CEntityForm<LeaseTermGuarantor> {

        public GuarantorInLeaseEditor() {
            super(LeaseTermGuarantor.class);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
            int row = -1;
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseParticipant().participantId()), 7).build());
            main.setWidget(++row, 0, 2, inject(proto().leaseParticipant().customer().person().name(), new NameEditor(i18n.tr("Guarantor"), Guarantor.class) {
                @Override
                public Key getLinkKey() {
                    return GuarantorInLeaseEditor.this.getValue().leaseParticipant().getPrimaryKey();
                }
            }));
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseParticipant().customer().person().sex()), 7).build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseParticipant().customer().person().birthDate()), 9).build());
            if (isEditable()) {
                main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().tenant(), new CSimpleEntityComboBox<Tenant>()), 25).build());
            } else {
                main.setWidget(++row, 0,
                        new FormDecoratorBuilder(inject(proto().tenant(), new CEntityCrudHyperlink<Tenant>(AppPlaceEntityMapper.resolvePlace(Tenant.class))))
                                .build());
            }

            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().relationship()), 15).build());
            main.setWidget(
                    ++row,
                    0,
                    new FormDecoratorBuilder(inject(proto().effectiveScreening(),
                            new CEntityCrudHyperlink<LeaseParticipantScreeningTO>(AppPlaceEntityMapper.resolvePlace(LeaseParticipantScreeningTO.class))), 9)
                            .build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseParticipant().customer().person().email()), 25).build());

            row = 1;
            main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().leaseParticipant().customer().person().homePhone()), 15).build());
            main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().leaseParticipant().customer().person().mobilePhone()), 15).build());
            main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().leaseParticipant().customer().person().workPhone()), 15).build());

            return main;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            get(proto().effectiveScreening()).setVisible(!getValue().effectiveScreening().isNull());

            if (isEditable()) {
                ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.guarantor, get(proto().leaseParticipant().participantId()), getValue()
                        .getPrimaryKey());

                get(proto().leaseParticipant().customer().person().birthDate()).setMandatory(getEnforceAgeOfMajority());
                get(proto().leaseParticipant().customer().person().email()).setMandatory(!getValue().leaseParticipant().customer().user().isNull());

                updateTenantList();
            }
        }

        @Override
        public void addValidations() {
            super.addValidations();

            get(proto().leaseParticipant().customer().person().birthDate()).addComponentValidator(new BirthdayDateValidator());
            get(proto().leaseParticipant().customer().person().birthDate()).addComponentValidator(new AbstractComponentValidator<LogicalDate>() {
                @Override
                public FieldValidationError isValid() {
                    if (getValue() != null && !getValue().leaseParticipant().customer().person().birthDate().isNull()) {
                        if (getEnforceAgeOfMajority()) {
                            if (!TimeUtils.isOlderThan(getValue().leaseParticipant().customer().person().birthDate().getValue(), getAgeOfMajority())) {
                                return new FieldValidationError(getComponent(), i18n.tr("The minimum age requirement for a guarantor is {0}.",
                                        getAgeOfMajority()));
                            }
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

        void setEnforceAgeOfMajority(Boolean enforceAgeOfMajority) {
            get(proto().leaseParticipant().customer().person().birthDate()).setMandatory(enforceAgeOfMajority);
        }

        void updateTenantList() {
            if (get(proto().tenant()) instanceof CComboBox<?>) {
                CComboBox<Tenant> combo = (CComboBox<Tenant>) get(proto().tenant());
                combo.setOptions(getLeaseCustomerTenants());
            }
        }

        private List<Tenant> getLeaseCustomerTenants() {
            List<Tenant> tenants = new ArrayList<Tenant>();
            for (LeaseTermTenant t : getLeaseTermTenants()) {
                tenants.add(t.leaseParticipant());
            }
            return tenants;
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