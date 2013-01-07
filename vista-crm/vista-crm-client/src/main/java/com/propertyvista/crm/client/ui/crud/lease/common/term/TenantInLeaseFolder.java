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
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
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

    public TenantInLeaseFolder() {
        this(false);
    }

    public TenantInLeaseFolder(boolean modifiable) {
        super(LeaseTermTenant.class, modifiable);
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
        decor.setExpended(false);
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

        tenant.leaseTermV().setPrimaryKey(getParentKey());
        tenant.leaseTermV().setValueDetached();
        if (!isApplicantPresent()) {
            tenant.role().setValue(LeaseTermParticipant.Role.Applicant);
            tenant.relationship().setValue(PersonRelationship.Other); // just do not leave it empty - it's mandatory field!
        }
        tenant.percentage().setValue(calcPercentage());

        assert (tenant.leaseTermV().isNull());
        return tenant;
    }

    private BigDecimal calcPercentage() {
        BigDecimal prc = new BigDecimal(1);
        for (LeaseTermTenant til : getValue()) {
            prc = prc.subtract(til.percentage().isNull() ? BigDecimal.ZERO : til.percentage().getValue());
        }
        return (prc.signum() > 0 ? prc : BigDecimal.ZERO);
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
                        return new ValidationError(component, i18n.tr("At least one Applicant should be present!"));
                    }
                }
                return null;
            }
        });

        this.addValueValidator(new EditableValueValidator<IList<LeaseTermTenant>>() {
            @Override
            public ValidationError isValid(CComponent<IList<LeaseTermTenant>, ?> component, IList<LeaseTermTenant> value) {
                if (value != null) {
                    if (!value.isEmpty()) {
                        BigDecimal totalPrc = BigDecimal.ZERO;
                        for (LeaseTermTenant item : value) {
                            if (item.percentage().getValue() != null) {
                                totalPrc = totalPrc.add(item.percentage().getValue());
                            }
                        }
                        return (totalPrc.compareTo(new BigDecimal(1)) == 0 ? null : new ValidationError(component, i18n
                                .tr("Sum of all percentages should be equal to 100%!")));
                    } else {
                        return new ValidationError(component, i18n.tr("At least one Applicant should be present!"));
                    }
                }
                return null;
            }
        });
    }

    private class TenantInLeaseEditor extends CEntityDecoratableForm<LeaseTermTenant> {

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
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().percentage()), 5).build());
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
                        if (event.getValue() == LeaseTermParticipant.Role.Dependent) {
                            get(proto().percentage()).setValue(BigDecimal.ZERO);
                        }
                        get(proto().percentage()).setEditable(event.getValue() != LeaseTermParticipant.Role.Dependent);
                        get(proto().relationship()).setVisible(event.getValue() != LeaseTermParticipant.Role.Applicant);
                    }
                });

// TODO: disable age restriction tweaks so far...                 
//                get(proto().leaseParticipant().customer().person().birthDate()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {
//                    @Override
//                    public void onValueChange(ValueChangeEvent<LogicalDate> event) {
//                        if (event.getValue() != null) {
//                            boolean mature = ValidationUtils.isOlderThen18(event.getValue());
//
//                            if (!mature) {
//                                get(proto().role()).setValue(LeaseTermParticipant.Role.Dependent);
//                                get(proto().percentage()).setValue(BigDecimal.ZERO);
//                            }
//                            get(proto().role()).setEditable(mature);
//                            get(proto().percentage()).setEditable(mature);
//                        }
//                    }
//                });
            }

            // assemble main panel:
            main.setWidget(0, 0, left);
            main.setWidget(0, 1, right);

            main.getColumnFormatter().setWidth(0, "50%");
            main.getColumnFormatter().setWidth(1, "50%");

            return main;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            get(proto().effectiveScreening()).setVisible(!getValue().effectiveScreening().isNull());

            if (isEditable()) {
                ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.tenant, get(proto().leaseParticipant().participantId()), getValue()
                        .getPrimaryKey());

                get(proto().leaseParticipant().customer().person().email()).setMandatory(!getValue().leaseParticipant().customer().user().isNull());

                if (get(proto().role()) instanceof CComboBox) {
                    CComboBox<Role> role = (CComboBox<Role>) get(proto().role());
                    role.setOptions(Role.tenantRelated());
                }

                get(proto().percentage()).setEditable(getValue().role().getValue() != LeaseTermParticipant.Role.Dependent);

// TODO: disable age restriction tweaks so far...                 
//                if (!getValue().leaseParticipant().customer().person().birthDate().isNull()) {
//                    if (!ValidationUtils.isOlderThen18(getValue().leaseParticipant().customer().person().birthDate().getValue())) {
//                        get(proto().role()).setEditable(false);
//                    }
//                }
            }

            get(proto().relationship()).setVisible(getValue().role().getValue() != LeaseTermParticipant.Role.Applicant);
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
}