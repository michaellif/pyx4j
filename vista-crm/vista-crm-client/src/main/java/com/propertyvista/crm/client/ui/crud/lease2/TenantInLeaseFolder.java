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
package com.propertyvista.crm.client.ui.crud.lease2;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.Tenant_2;
import com.propertyvista.domain.tenant.lease.LeaseParticipant_2;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.dto.LeaseTermDTO;

public class TenantInLeaseFolder extends LeaseParticipantFolder<Tenant_2> {

    static final I18n i18n = I18n.get(TenantInLeaseFolder.class);

    private final CEntityForm<LeaseTermDTO> leaseTerm;

    public TenantInLeaseFolder(CEntityForm<LeaseTermDTO> parent, boolean modifiable) {
        super(Tenant_2.class, modifiable);
        this.leaseTerm = parent;
    }

    @Override
    protected String getAddItemDialogCaption() {
        return i18n.tr("Add New Tenant_2");
    }

    @Override
    protected String getAddItemDialogBody() {
        return i18n.tr("Do you want to select existing Tenant?");
    }

    @Override
    protected void addParticipants(List<Customer> customers) {
        for (Customer customer : customers) {
            Tenant_2 newTenantInLease = EntityFactory.create(Tenant_2.class);
            newTenantInLease.leaseTermV().setPrimaryKey(leaseTerm.getValue().version().getPrimaryKey());
            newTenantInLease.customer().set(customer);
            if (!isApplicantPresent()) {
                newTenantInLease.role().setValue(LeaseParticipant_2.Role.Applicant);
                newTenantInLease.relationship().setValue(PersonRelationship.Other); // just not leave it empty - it's mandatory field!
            }
            newTenantInLease.percentage().setValue(calcPercentage());
            addItem(newTenantInLease);
        }
    }

    private boolean isApplicantPresent() {
        for (Tenant_2 til : getValue()) {
            if (til.role().getValue() == LeaseParticipant_2.Role.Applicant) {
                return true;
            }
        }
        return false;
    }

    private BigDecimal calcPercentage() {
        BigDecimal prc = new BigDecimal(1);
        for (Tenant_2 til : getValue()) {
            prc = prc.subtract(til.percentage().isNull() ? BigDecimal.ZERO : til.percentage().getValue());
        }
        return (prc.signum() > 0 ? prc : BigDecimal.ZERO);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof Tenant_2) {
            return new TenantInLeaseEditor();
        }
        return super.create(member);
    }

    @Override
    public void addValidations() {

        this.addValueValidator(new EditableValueValidator<List<Tenant_2>>() {
            @Override
            public ValidationError isValid(CComponent<List<Tenant_2>, ?> component, List<Tenant_2> value) {
                if (value != null) {
                    boolean applicant = false;
                    for (Tenant_2 item : value) {
                        if (applicant) {
                            if (item.role().getValue() == LeaseParticipant_2.Role.Applicant) {
                                return new ValidationError(component, i18n.tr("Just one applicant could be selected!"));
                            }
                        } else {
                            applicant = (item.role().getValue() == LeaseParticipant_2.Role.Applicant);
                        }
                    }
                }
                return null;
            }
        });

        this.addValueValidator(new EditableValueValidator<IList<Tenant_2>>() {
            @Override
            public ValidationError isValid(CComponent<IList<Tenant_2>, ?> component, IList<Tenant_2> value) {
                if (value != null) {
                    if (!value.isEmpty()) {
                        BigDecimal totalPrc = BigDecimal.ZERO;
                        for (Tenant_2 item : value) {
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

    private class TenantInLeaseEditor extends CEntityDecoratableForm<Tenant_2> {

        public TenantInLeaseEditor() {
            super(Tenant_2.class);
        }

        @SuppressWarnings("unchecked")
        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            FormFlexPanel left = new FormFlexPanel();
            int row = -1;
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().participantId()), 7).build());
            left.setWidget(++row, 0, inject(proto().customer().person().name(), new NameEditor(i18n.tr("Tenant"), Tenant_2.class) {
                @Override
                public Key getLinkKey() {
                    return TenantInLeaseEditor.this.getValue().getPrimaryKey();
                }
            }));
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().sex()), 7).build());
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().birthDate()), 9).build());

            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().role(), new CComboBox<LeaseParticipant_2.Role>()), 15).build());
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().relationship()), 15).build());
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().percentage()), 5).build());
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().screening()), 9).customLabel(i18n.tr("Use Screening From")).build());

            FormFlexPanel right = new FormFlexPanel();
            row = -1;
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().email()), 25).build());
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().homePhone()), 15).build());
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().mobilePhone()), 15).build());
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().workPhone()), 15).build());

            if (isEditable()) {
                get(proto().role()).addValueChangeHandler(new ValueChangeHandler<LeaseParticipant_2.Role>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<LeaseParticipant_2.Role> event) {
                        get(proto().relationship()).setVisible(event.getValue() != LeaseParticipant_2.Role.Applicant);
                        if (event.getValue() == LeaseParticipant_2.Role.Dependent) {
                            get(proto().percentage()).setValue(BigDecimal.ZERO);
                        }
                        get(proto().percentage()).setEditable(event.getValue() != LeaseParticipant_2.Role.Dependent);
                    }
                });

                get(proto().customer().person().birthDate()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                        if (event.getValue() != null) {
                            boolean mature = ValidationUtils.isOlderThen18(event.getValue());

                            if (!mature) {
                                get(proto().role()).setValue(LeaseParticipant_2.Role.Dependent);
                                get(proto().percentage()).setValue(BigDecimal.ZERO);
                            }
                            get(proto().role()).setEditable(mature);
                            get(proto().percentage()).setEditable(mature);
                        }
                    }
                });
            }

            // assemble main panel:
            main.setWidget(0, 0, left);
            main.setWidget(0, 1, right);

            main.getColumnFormatter().setWidth(0, "60%");
            main.getColumnFormatter().setWidth(1, "40%");

            return main;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            get(proto().customer().person().email()).setMandatory(!getValue().customer().user().isNull());

            if (isEditable()) {
                ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.tenant, get(proto().participantId()), getValue().getPrimaryKey());
            }

            boolean applicant = (getValue().role().getValue() == LeaseParticipant_2.Role.Applicant);
            if (applicant) {
                get(proto().role()).setViewable(true);
                get(proto().relationship()).setVisible(false);
            } else if (isEditable()) {
                Collection<LeaseParticipant_2.Role> roles = LeaseParticipant_2.Role.tenantRelated();
                if (getValue().role().getValue() != null) { // if not new entity creation...
                    roles.remove(LeaseParticipant_2.Role.Applicant);
                }
                ((CComboBox<LeaseParticipant_2.Role>) get(proto().role())).setOptions(roles);

                get(proto().percentage()).setEditable(getValue().role().getValue() != LeaseParticipant_2.Role.Dependent);

                if (!getValue().customer().person().birthDate().isNull()) {
                    if (!ValidationUtils.isOlderThen18(getValue().customer().person().birthDate().getValue())) {
                        get(proto().role()).setEditable(false);
                    }
                }
            }

            if (get(proto().screening()) instanceof CEntityComboBox<?>) {
                CEntityComboBox<PersonScreening> combo = (CEntityComboBox<PersonScreening>) get(proto().screening());
                combo.resetCriteria();
                combo.addCriterion(PropertyCriterion.eq(combo.proto().screene(), getValue().customer()));
                combo.refreshOptions();
            }
        }

// TODO : implement percent recalculation logic         
//        @Override
//        protected void propagateValue(Tenant_2 entity, boolean fireEvent, boolean populate) {
//            super.propagateValue(entity, fireEvent, populate);
//            if ((getValue().role().getValue() == Role.Applicant)) {
//                get(proto().percentage()).setEditable(false);
//                get(proto().percentage()).setViewable(true);
//            }
//        }
    }
}