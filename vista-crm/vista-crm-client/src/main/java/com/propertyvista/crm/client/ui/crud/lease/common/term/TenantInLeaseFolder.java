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

import java.math.BigDecimal;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
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
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.ItemActionsBar.ActionType;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.IPane;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.components.folders.PapCoveredItemFolder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.visor.paps.PreauthorizedPaymentsVisorController;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;

public class TenantInLeaseFolder extends LeaseTermParticipantFolder<LeaseTermTenant> {

    static final I18n i18n = I18n.get(TenantInLeaseFolder.class);

    private boolean isPadEditable = false;

    private final IPane parentView;

    public TenantInLeaseFolder(IPane parentView) {
        this(false, parentView);
    }

    public TenantInLeaseFolder(boolean modifiable, IPane parentView) {
        super(LeaseTermTenant.class, modifiable);
        this.parentView = parentView;
    }

    public void setPadEditable(boolean isPadEditable) {
        this.isPadEditable = isPadEditable;
    }

    @Override
    protected String getAddItemDialogCaption() {
        return i18n.tr("Add New Tenant");
    }

    @Override
    protected String getAddItemDialogBody() {
        return i18n.tr("Do you want to select existing Tenant?");
    }

    @Override
    public IFolderItemDecorator<LeaseTermTenant> createItemDecorator() {
        BoxFolderItemDecorator<LeaseTermTenant> decor = (BoxFolderItemDecorator<LeaseTermTenant>) super.createItemDecorator();
        decor.setExpended(isEditable() || isPadEditable);
        return decor;
    }

    @Override
    protected CEntityFolderItem<LeaseTermTenant> createItem(boolean first) {
        final CEntityFolderItem<LeaseTermTenant> item = super.createItem(first);

        if (isPadEditable) {
            item.addAction(ActionType.Cust1, i18n.tr("Edit PAPs"), CrmImages.INSTANCE.editButton(), new Command() {
                @Override
                public void execute() {
                    new PreauthorizedPaymentsVisorController(parentView, item.getValue().leaseParticipant().getPrimaryKey()) {
                        @Override
                        public boolean onClose(List<PreauthorizedPayment> pads) {
                            for (CComponent<?, ?> comp : item.getComponents()) {
                                if (comp instanceof TenantInLeaseEditor) {
                                    ((TenantInLeaseEditor) comp).get((((TenantInLeaseEditor) comp).proto().leaseParticipant().preauthorizedPayments()))
                                            .setValue(pads);
                                }
                            }
                            return true;
                        }

                    }.show();
                }
            });
        }

        return item;
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

        tenant.leaseTermV().setPrimaryKey(getParentKey());
        tenant.leaseTermV().setValueDetached();
        if (!isApplicantPresent()) {
            tenant.role().setValue(LeaseTermParticipant.Role.Applicant);
            tenant.relationship().setValue(PersonRelationship.Other); // just do not leave it empty - it's mandatory field!
        }

//        assert (!tenant.leaseTermV().isNull());
        return tenant;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof LeaseTermTenant) {
            return new TenantInLeaseEditor();
        }
        return super.create(member);
    }

    @Override
    public void addValidations() {

        this.addValueValidator(new EditableValueValidator<List<LeaseTermTenant>>() {
            @Override
            public ValidationError isValid(CComponent<List<LeaseTermTenant>, ?> component, List<LeaseTermTenant> value) {
                if (value != null) {
                    boolean applicant = false;
                    for (LeaseTermTenant item : value) {
                        if (applicant) {
                            if (item.role().getValue() == LeaseTermParticipant.Role.Applicant) {
                                return new ValidationError(component, i18n.tr("Just one Applicant could be selected!"));
                            }
                        } else {
                            applicant = (item.role().getValue() == LeaseTermParticipant.Role.Applicant);
                        }
                    }
                    if (!applicant) {
                        return new ValidationError(component, i18n.tr("Applicant should be present!"));
                    }
                }
                return null;
            }
        });

        this.addValueValidator(new EditableValueValidator<IList<LeaseTermTenant>>() {
            @Override
            public ValidationError isValid(CComponent<IList<LeaseTermTenant>, ?> component, IList<LeaseTermTenant> value) {
                if (value != null) {
                    if (value.isEmpty()) {
                        return new ValidationError(component, i18n.tr("At least one Tenant should be present!"));
                    }
                }
                return null;
            }
        });
    }

    private class TenantInLeaseEditor extends CEntityDecoratableForm<LeaseTermTenant> {

        private final FormFlexPanel preauthorizedPaymentsPanel = new FormFlexPanel();

        public TenantInLeaseEditor() {
            super(LeaseTermTenant.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            FormFlexPanel left = new FormFlexPanel();
            int row = -1;
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseParticipant().participantId()), 7).build());
            left.setWidget(++row, 0, inject(proto().leaseParticipant().customer().person().name(), new NameEditor(i18n.tr("Tenant"), Tenant.class) {
                @Override
                public Key getLinkKey() {
                    return TenantInLeaseEditor.this.getValue().leaseParticipant().getPrimaryKey();
                }
            }));
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseParticipant().customer().person().sex()), 7).build());
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseParticipant().customer().person().birthDate()), 9).build());

            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().role()), 15).build());
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().relationship()), 15).build());
            left.setWidget(
                    ++row,
                    0,
                    new DecoratorBuilder(inject(proto().effectiveScreening(),
                            new CEntityCrudHyperlink<CustomerScreening>(AppPlaceEntityMapper.resolvePlace(CustomerScreening.class))), 9).build());

            FormFlexPanel right = new FormFlexPanel();
            row = -1;
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseParticipant().customer().person().email()), 25).build());
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseParticipant().customer().person().homePhone()), 15).build());
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseParticipant().customer().person().mobilePhone()), 15).build());
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseParticipant().customer().person().workPhone()), 15).build());

            if (isEditable()) {
                get(proto().role()).addValueChangeHandler(new ValueChangeHandler<LeaseTermParticipant.Role>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<LeaseTermParticipant.Role> event) {
                        get(proto().relationship()).setVisible(event.getValue() != LeaseTermParticipant.Role.Applicant);
                    }
                });

                get(proto().role()).addValueValidator(new EditableValueValidator<LeaseTermParticipant.Role>() {
                    @Override
                    public ValidationError isValid(CComponent<LeaseTermParticipant.Role, ?> component, LeaseTermParticipant.Role role) {
                        if (getAgeOfMajority() != null) {
                            if (role != null && role == Role.Applicant) {
                                if (!TimeUtils.isOlderThan(getValue().leaseParticipant().customer().person().birthDate().getValue(), getAgeOfMajority() - 1)) {
                                    return new ValidationError(component, i18n.tr(
                                            "This tenant is too young to be an applicant: the minimum age required is {0}.", getAgeOfMajority()));
                                }
                            }
                        }
                        return null;
                    }
                });
            }

            preauthorizedPaymentsPanel.setH3(0, 0, 2, proto().leaseParticipant().preauthorizedPayments().getMeta().getCaption());
            preauthorizedPaymentsPanel.setWidget(1, 0, inject(proto().leaseParticipant().preauthorizedPayments(), new PreauthorizedPayments()));

            // assemble main panel:
            main.setWidget(0, 0, left);
            main.setWidget(0, 1, right);
            main.setWidget(1, 0, preauthorizedPaymentsPanel);
            main.getFlexCellFormatter().setColSpan(1, 0, 2);

            main.getColumnFormatter().setWidth(0, VistaTheme.columnWidth);
            left.setWidth(VistaTheme.columnWidth); // necessary for inner table columns to maintain fixed column width!

            return main;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            get(proto().effectiveScreening()).setVisible(!getValue().effectiveScreening().isNull());
            get(proto().relationship()).setVisible(getValue().role().getValue() != LeaseTermParticipant.Role.Applicant);
            preauthorizedPaymentsPanel.setVisible(!isEditable() /* && !getValue().leaseParticipant().preauthorizedPayments().isEmpty() */);

            if (isEditable()) {
                ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.tenant, get(proto().leaseParticipant().participantId()), getValue()
                        .getPrimaryKey());

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
            nameEditor.get(nameEditor.proto().firstName()).setValue("Firstname");
            nameEditor.get(nameEditor.proto().lastName()).setValue("Lastname");
            get(proto().leaseParticipant().customer().person().birthDate()).setValue(new LogicalDate(80, 1, 1));
            get(proto().role()).setValue(LeaseTermParticipant.Role.Applicant);
            get(proto().percentage()).setValue(new BigDecimal(1));
        }
    }

    private class PreauthorizedPayments extends VistaBoxFolder<PreauthorizedPayment> {

        public PreauthorizedPayments() {
            super(PreauthorizedPayment.class);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof PreauthorizedPayment) {
                return new PreauthorizedPaymentViewer();
            }
            return super.create(member);
        }

        private class PreauthorizedPaymentViewer extends CEntityDecoratableForm<PreauthorizedPayment> {

            public PreauthorizedPaymentViewer() {
                super(PreauthorizedPayment.class);
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel content = new FormFlexPanel();

                content.setWidget(0, 0, new DecoratorBuilder(inject(proto().paymentMethod()), 40, 10).build());
                content.setWidget(1, 0, inject(proto().coveredItems(), new PapCoveredItemFolder()));

                return content;
            }
        }
    }
}