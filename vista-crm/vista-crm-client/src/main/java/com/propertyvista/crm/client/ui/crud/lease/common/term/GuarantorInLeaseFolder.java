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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CSimpleEntityComboBox;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.LeaseParticipantScreeningTO;
import com.propertyvista.shared.config.VistaFeatures;

public class GuarantorInLeaseFolder extends LeaseTermParticipantFolder<LeaseTermGuarantor> {

    static final I18n i18n = I18n.get(GuarantorInLeaseFolder.class);

    public GuarantorInLeaseFolder(CrmEntityForm<?> parentForm) {
        super(LeaseTermGuarantor.class, parentForm);
    }

    @Override
    public void setEnforceAgeOfMajority(boolean enforceAgeOfMajority) {
        super.setEnforceAgeOfMajority(enforceAgeOfMajority);

        for (CComponent<?, ?, ?> comp : getComponents()) {
            ((GuarantorInLeaseEditor) ((CFolderItem<?>) comp).getComponents().iterator().next()).setEnforceAgeOfMajority(enforceAgeOfMajority);
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
    protected CForm<LeaseTermGuarantor> createItemForm(IObject<?> member) {
        return new GuarantorInLeaseEditor();
    }

    void updateTenantList() {
        for (CComponent<?, ?, ?> comp : getComponents()) {
            ((GuarantorInLeaseEditor) ((CFolderItem<?>) comp).getComponents().iterator().next()).updateTenantList();
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

    private class GuarantorInLeaseEditor extends CForm<LeaseTermGuarantor> {

        public GuarantorInLeaseEditor() {
            super(LeaseTermGuarantor.class);
        }

        @Override
        protected IsWidget createContent() {
            BasicCFormPanel formPanel = new BasicCFormPanel(this);

            formPanel.append(Location.Left, proto().leaseParticipant().participantId()).decorate().componentWidth(100);
            formPanel.append(Location.Full, proto().leaseParticipant().customer().person().name(), new NameEditor(i18n.tr("Guarantor"), Guarantor.class) {
                @Override
                public Key getLinkKey() {
                    return GuarantorInLeaseEditor.this.getValue().leaseParticipant().getPrimaryKey();
                }
            });
            formPanel.append(Location.Left, proto().leaseParticipant().customer().person().sex()).decorate().componentWidth(100);
            formPanel.append(Location.Left, proto().leaseParticipant().customer().person().birthDate()).decorate().componentWidth(120);
            if (isEditable()) {
                formPanel.append(Location.Left, proto().tenant(), new CSimpleEntityComboBox<Tenant>()).decorate();
            } else {
                formPanel.append(
                        Location.Left,
                        inject(proto().tenant(), new CEntityCrudHyperlink<Tenant>(AppPlaceEntityMapper.resolvePlace(Tenant.class)),
                                new FieldDecoratorBuilder().build()));
            }

            formPanel.append(Location.Left, proto().relationship()).decorate().componentWidth(180);
            formPanel.append(
                    Location.Left,
                    inject(proto().effectiveScreening(),
                            new CEntityCrudHyperlink<LeaseParticipantScreeningTO>(AppPlaceEntityMapper.resolvePlace(LeaseParticipantScreeningTO.class)),
                            new FieldDecoratorBuilder(9).build()));
            formPanel.append(Location.Left, proto().leaseParticipant().customer().person().email()).decorate();

            formPanel.append(Location.Right, proto().leaseParticipant().yardiApplicantId()).decorate().componentWidth(120);
            formPanel.append(Location.Right, proto().leaseParticipant().customer().person().homePhone()).decorate().componentWidth(180);
            formPanel.append(Location.Right, proto().leaseParticipant().customer().person().mobilePhone()).decorate().componentWidth(180);
            formPanel.append(Location.Right, proto().leaseParticipant().customer().person().workPhone()).decorate().componentWidth(180);

            return formPanel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            get(proto().leaseParticipant().yardiApplicantId()).setVisible(VistaFeatures.instance().yardiIntegration());

            get(proto().effectiveScreening()).setVisible(!getValue().effectiveScreening().isNull());

            if (isEditable()) {
                if (VistaFeatures.instance().yardiIntegration()) {
                    get(proto().leaseParticipant().participantId()).setVisible(false);
                    get(proto().leaseParticipant().yardiApplicantId()).setVisible(false);
                } else {
                    ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.guarantor, get(proto().leaseParticipant().participantId()), getValue()
                            .getPrimaryKey());
                }

                get(proto().leaseParticipant().customer().person().birthDate()).setMandatory(getEnforceAgeOfMajority());
                get(proto().leaseParticipant().customer().person().email()).setMandatory(!getValue().leaseParticipant().customer().user().isNull());

                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        updateTenantList();
                    }
                });
            }
        }

        @Override
        public void addValidations() {
            super.addValidations();

            get(proto().leaseParticipant().customer().person().birthDate()).addComponentValidator(new BirthdayDateValidator());
            get(proto().leaseParticipant().customer().person().birthDate()).addComponentValidator(new AbstractComponentValidator<LogicalDate>() {
                @Override
                public FieldValidationError isValid() {
                    if (getComponent().getValue() != null) {
                        if (getEnforceAgeOfMajority()) {
                            if (!TimeUtils.isOlderThan(getComponent().getValue(), getAgeOfMajority())) {
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

        @SuppressWarnings("unchecked")
        void updateTenantList() {
            if (get(proto().tenant()) instanceof CComboBox<?>) {
                ((CComboBox<Tenant>) get(proto().tenant())).setOptions(getLeaseTenants());
            }
        }

        private List<Tenant> getLeaseTenants() {
            List<Tenant> tenants = new ArrayList<Tenant>();
            for (LeaseTermTenant t : getLeaseTermTenants()) {
                if (!Role.Dependent.equals(t.role().getValue())) {
                    tenants.add(t.leaseParticipant());
                }
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