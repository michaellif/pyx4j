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

import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.AbstractValidationError;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.components.folders.PapCoveredItemFolder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.security.common.AbstractPmcUser;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.LeaseParticipantScreeningTO;
import com.propertyvista.shared.config.VistaFeatures;

public class TenantInLeaseFolder extends LeaseTermParticipantFolder<LeaseTermTenant> {

    static final I18n i18n = I18n.get(TenantInLeaseFolder.class);

    private boolean isPadEditable = false;

    private boolean maturedOccupantsAreApplicant = false;

    public TenantInLeaseFolder(CrmEntityForm<?> parentForm) {
        super(LeaseTermTenant.class, parentForm);
    }

    public void setPadEditable(boolean isPadEditable) {
        this.isPadEditable = isPadEditable;
    }

    @Override
    public void setEnforceAgeOfMajority(boolean enforceAgeOfMajority) {
        super.setEnforceAgeOfMajority(enforceAgeOfMajority);

        for (CComponent<?, ?, ?> comp : getComponents()) {
            ((TenantInLeaseEditor) ((CFolderItem<?>) comp).getComponents().iterator().next()).setEnforceAgeOfMajority(enforceAgeOfMajority);
        }
    }

    @Override
    protected String getAddItemDialogCaption() {
        return i18n.tr("Add New Prospect/Tenant");
    }

    @Override
    protected String getAddItemDialogBody() {
        return i18n.tr("Do you want to select existing Prospect/Tenant?");
    }

    public boolean getMaturedOccupantsAreApplicants() {
        return maturedOccupantsAreApplicant;
    }

    public void setMaturedOccupantsAreApplicants(boolean maturedOccupantsAreApplicants) {
        this.maturedOccupantsAreApplicant = maturedOccupantsAreApplicants;
    }

    @Override
    public IFolderItemDecorator<LeaseTermTenant> createItemDecorator() {
        BoxFolderItemDecorator<LeaseTermTenant> decor = (BoxFolderItemDecorator<LeaseTermTenant>) super.createItemDecorator();
        decor.setExpended(isEditable() || isPadEditable);
        return decor;
    }

    @Override
    protected void addParticipants(List<Customer> customers) {
        for (Customer customer : customers) {
            LeaseTermTenant newTenant = createTenant();
            newTenant.leaseParticipant().customer().set(customer);
            addItem(newTenant);
        }
    }

    @Override
    protected void addParticipant() {
        addItem(createTenant());
    }

    private boolean isApplicantPresent() {
        for (LeaseTermTenant tenant : getValue()) {
            if (tenant.role().getValue() == LeaseTermParticipant.Role.Applicant) {
                return true;
            }
        }
        return false;
    }

    LeaseTermTenant createTenant() {
        LeaseTermTenant tenant = EntityFactory.create(LeaseTermTenant.class);

        tenant.leaseTermV().setValueDetached();
        if (!isApplicantPresent()) {
            tenant.role().setValue(LeaseTermParticipant.Role.Applicant);
            tenant.relationship().setValue(PersonRelationship.Other); // just do not leave it empty - it's mandatory field!
        }

        return tenant;
    }

    @Override
    protected CForm<LeaseTermTenant> createItemForm(IObject<?> member) {
        return new TenantInLeaseEditor();
    }

    @Override
    public void addValidations() {

        this.addComponentValidator(new AbstractComponentValidator<IList<LeaseTermTenant>>() {
            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() != null) {
                    boolean applicant = false;
                    for (LeaseTermTenant item : getComponent().getValue()) {
                        if (applicant) {
                            if (item.role().getValue() == LeaseTermParticipant.Role.Applicant) {
                                return new FieldValidationError(getComponent(), i18n.tr("Just one person with role 'Tenant' could be selected!"));
                            }
                        } else {
                            applicant = (item.role().getValue() == LeaseTermParticipant.Role.Applicant);
                        }
                    }
                    if (!applicant) {
                        return new FieldValidationError(getComponent(), i18n.tr("A person with role 'Tenant' should be present!"));
                    }
                }
                return null;
            }
        });

        this.addComponentValidator(new AbstractComponentValidator<IList<LeaseTermTenant>>() {
            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() != null) {
                    if (getComponent().getValue().isEmpty()) {
                        return new FieldValidationError(getComponent(), i18n.tr("At least one Person should be present!"));
                    }
                }
                return null;
            }
        });
    }

    private class TenantInLeaseEditor extends CForm<LeaseTermTenant> {

        private final BasicCFormPanel preauthorizedPaymentsPanel = new BasicCFormPanel(this);

        private final PreauthorizedPayments preauthorizedPayments = new PreauthorizedPayments();

        public TenantInLeaseEditor() {
            super(LeaseTermTenant.class);
        }

        @Override
        protected IsWidget createContent() {
            BasicCFormPanel formPanel = new BasicCFormPanel(this);

            formPanel.append(Location.Left, proto().leaseParticipant().participantId()).decorate().componentWidth(100);
            formPanel.append(Location.Full, proto().leaseParticipant().customer().person().name(), new NameEditor(i18n.tr("Tenant"), Tenant.class) {
                @Override
                public Key getLinkKey() {
                    return TenantInLeaseEditor.this.getValue().leaseParticipant().getPrimaryKey();
                }
            });
            formPanel.append(Location.Left, proto().leaseParticipant().customer().person().sex()).decorate().componentWidth(100);
            formPanel.append(Location.Left, proto().leaseParticipant().customer().person().birthDate()).decorate().componentWidth(120);

            formPanel.append(Location.Left, proto().role()).decorate().componentWidth(180);
            formPanel.append(Location.Left, proto().relationship()).decorate().componentWidth(180);
            formPanel
                    .append(Location.Left, proto().effectiveScreening(),
                            new CEntityCrudHyperlink<LeaseParticipantScreeningTO>(AppPlaceEntityMapper.resolvePlace(LeaseParticipantScreeningTO.class)))
                    .decorate().componentWidth(120);
            formPanel.append(Location.Left, proto().leaseParticipant().customer().person().email()).decorate();

            formPanel.append(Location.Right, proto().leaseParticipant().yardiApplicantId()).decorate().componentWidth(120);
            formPanel.br();
            formPanel.append(Location.Right, proto().leaseParticipant().customer().person().homePhone()).decorate().componentWidth(180);
            formPanel.append(Location.Right, proto().leaseParticipant().customer().person().mobilePhone()).decorate().componentWidth(180);
            formPanel.append(Location.Right, proto().leaseParticipant().customer().person().workPhone()).decorate().componentWidth(180);

            preauthorizedPaymentsPanel.h3(proto().leaseParticipant().preauthorizedPayments().getMeta().getCaption());
            preauthorizedPaymentsPanel.append(Location.Full, inject(proto().leaseParticipant().preauthorizedPayments(), preauthorizedPayments));

            formPanel.append(Location.Full, preauthorizedPaymentsPanel);

            return formPanel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            get(proto().leaseParticipant().yardiApplicantId()).setVisible(VistaFeatures.instance().yardiIntegration());

            get(proto().effectiveScreening()).setVisible(!getValue().effectiveScreening().isNull());
            get(proto().relationship()).setVisible(getValue().role().getValue() != LeaseTermParticipant.Role.Applicant);
            preauthorizedPaymentsPanel.setVisible(!isEditable() && !getValue().leaseParticipant().preauthorizedPayments().isEmpty());

            if (isEditable()) {
                if (VistaFeatures.instance().yardiIntegration()) {
                    get(proto().leaseParticipant().participantId()).setVisible(false);
                    get(proto().leaseParticipant().yardiApplicantId()).setVisible(false);
                } else {
                    ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.tenant, get(proto().leaseParticipant().participantId()), getValue()
                            .getPrimaryKey());
                }

                get(proto().leaseParticipant().customer().person().birthDate()).setMandatory(getEnforceAgeOfMajority());
                get(proto().leaseParticipant().customer().person().email()).setMandatory(!getValue().leaseParticipant().customer().user().isNull());

                if (get(proto().role()) instanceof CComboBox) {
                    CComboBox<Role> role = (CComboBox<Role>) get(proto().role());
                    role.setOptions(Role.tenantRelated());
                }
            }
        }

        @Override
        public void addValidations() {
            super.addValidations();

            get(proto().leaseParticipant().customer().person().birthDate()).addComponentValidator(new BirthdayDateValidator());
            get(proto().leaseParticipant().customer().person().birthDate()).addComponentValidator(new AbstractComponentValidator<LogicalDate>() {
                @Override
                public AbstractValidationError isValid() {
                    get(proto().role()).revalidate();
                    return null;
                }
            });
            get(proto().leaseParticipant().customer().person().birthDate()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().role())));

            get(proto().role()).addComponentValidator(new AbstractComponentValidator<LeaseTermParticipant.Role>() {
                @Override
                public FieldValidationError isValid() {
                    if (getComponent().getValue() != null && getValue() != null && !getValue().leaseParticipant().customer().person().birthDate().isNull()) {
                        if (getEnforceAgeOfMajority()) {
                            if (Role.resposible().contains(getComponent().getValue())) {
                                if (!TimeUtils.isOlderThan(getValue().leaseParticipant().customer().person().birthDate().getValue(), getAgeOfMajority())) {
                                    return new FieldValidationError(
                                            getComponent(),
                                            i18n.tr("This person is too young to be a tenant or a co-tenant: the minimum age required is {0}. Please mark the person as a Dependent instead",
                                                    getAgeOfMajority()));
                                }
                            }
                        }
                        if (getMaturedOccupantsAreApplicants()) {
                            if (Role.Dependent == getComponent().getValue()) {
                                if (TimeUtils.isOlderThan(getValue().leaseParticipant().customer().person().birthDate().getValue(), getAgeOfMajority())) {
                                    return new FieldValidationError(getComponent(), i18n
                                            .tr("According to internal regulations and age this person cannot be a Dependent"));
                                }
                            }
                        }
                    }
                    return null;
                }
            });
            get(proto().role()).addValueChangeHandler(new ValueChangeHandler<LeaseTermParticipant.Role>() {
                @Override
                public void onValueChange(ValueChangeEvent<LeaseTermParticipant.Role> event) {
                    get(proto().relationship()).setVisible(event.getValue() != LeaseTermParticipant.Role.Applicant);
                    if (getParentForm() instanceof LeaseTermForm) {
                        ((LeaseTermForm) getParentForm()).getGuarantorsFolder().updateTenantList();
                    }
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

        void setEnforceAgeOfMajority(boolean enforceAgeOfMajority) {
            get(proto().leaseParticipant().customer().person().birthDate()).setMandatory(enforceAgeOfMajority);
        }

        private void devGenerateTenant() {
            NameEditor nameEditor = (NameEditor) get(proto().leaseParticipant().customer().person().name());
            nameEditor.get(nameEditor.proto().firstName()).setValue("Firstname");
            nameEditor.get(nameEditor.proto().lastName()).setValue("Lastname");
            get(proto().leaseParticipant().customer().person().birthDate()).setValue(new LogicalDate(80, 1, 1));
            get(proto().role()).setValue(LeaseTermParticipant.Role.Applicant);
        }

        void setPreauthorizedPayments(List<AutopayAgreement> pads) {
            getValue().leaseParticipant().preauthorizedPayments().clear();
            getValue().leaseParticipant().preauthorizedPayments().addAll(pads);
            preauthorizedPayments.setValue(getValue().leaseParticipant().preauthorizedPayments());
        }
    }

    private class PreauthorizedPayments extends VistaBoxFolder<AutopayAgreement> {

        public PreauthorizedPayments() {
            super(AutopayAgreement.class);
        }

        @Override
        protected CForm<AutopayAgreement> createItemForm(IObject<?> member) {
            return new PreauthorizedPaymentViewer();
        }

        private class PreauthorizedPaymentViewer extends CForm<AutopayAgreement> {

            public PreauthorizedPaymentViewer() {
                super(AutopayAgreement.class);
            }

            @Override
            protected IsWidget createContent() {
                BasicCFormPanel formPanel = new BasicCFormPanel(this);

                formPanel.append(Location.Left, proto().id(), new CNumberLabel()).decorate().componentWidth(120);

                formPanel.append(Location.Left, proto().creationDate()).decorate().componentWidth(120);
                formPanel.append(Location.Right, proto().createdBy(), new CEntityLabel<AbstractPmcUser>()).decorate();

                formPanel.append(Location.Left, proto().paymentMethod()).decorate();

                formPanel.append(Location.Full, proto().coveredItems(), new PapCoveredItemFolder());

                return formPanel;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                get(proto().id()).setVisible(!getValue().id().isNull());
                get(proto().creationDate()).setVisible(!getValue().creationDate().isNull());
                get(proto().createdBy()).setVisible(!getValue().createdBy().isNull());
            }
        }
    }
}