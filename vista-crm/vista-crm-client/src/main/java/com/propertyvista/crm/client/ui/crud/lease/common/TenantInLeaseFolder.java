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
package com.propertyvista.crm.client.ui.crud.lease.common;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CNumberField;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.dto.LeaseDTO;

public class TenantInLeaseFolder extends LeaseParticipantFolder<Tenant> {

    static final I18n i18n = I18n.get(TenantInLeaseFolder.class);

    private final CEntityForm<? extends LeaseDTO> lease;

    public TenantInLeaseFolder(CEntityForm<? extends LeaseDTO> parent, boolean modifiable) {
        super(Tenant.class, modifiable);
        this.lease = parent;
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
    protected void addParticipants(List<Customer> customers) {
        for (Customer tenant : customers) {
            Tenant newTenantInLease = EntityFactory.create(Tenant.class);
            newTenantInLease.leaseV().setPrimaryKey(lease.getValue().version().getPrimaryKey());
            newTenantInLease.customer().set(tenant);
            if (!isApplicantPresent()) {
                newTenantInLease.role().setValue(LeaseParticipant.Role.Applicant);
                newTenantInLease.relationship().setValue(PersonRelationship.Other); // just not leave it empty - it's mandatory field!
            }
            newTenantInLease.percentage().setValue(calcPercentage());
            addItem(newTenantInLease);
        }
    }

    private boolean isApplicantPresent() {
        for (Tenant til : getValue()) {
            if (til.role().getValue() == LeaseParticipant.Role.Applicant) {
                return true;
            }
        }
        return false;
    }

    private Integer calcPercentage() {
        Integer prc = 100;
        for (Tenant til : getValue()) {
            prc -= (til.percentage().isNull() ? 0 : til.percentage().getValue());
        }
        return (prc > 0 ? prc : 0);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof Tenant) {
            return new TenantInLeaseEditor();
        }
        return super.create(member);
    }

    @Override
    public void addValidations() {

        this.addValueValidator(new EditableValueValidator<List<Tenant>>() {
            @Override
            public ValidationFailure isValid(CComponent<List<Tenant>, ?> component, List<Tenant> value) {
                if (value != null) {
                    boolean applicant = false;
                    for (Tenant item : value) {
                        if (applicant) {
                            if (item.role().getValue() == LeaseParticipant.Role.Applicant) {
                                return new ValidationFailure(i18n.tr("Just one applicant could be selected!"));
                            }
                        } else {
                            applicant = (item.role().getValue() == LeaseParticipant.Role.Applicant);
                        }
                    }
                }
                return null;
            }
        });

        this.addValueValidator(new EditableValueValidator<IList<Tenant>>() {
            @Override
            public ValidationFailure isValid(CComponent<IList<Tenant>, ?> component, IList<Tenant> value) {
                if (value != null) {
                    if (!value.isEmpty()) {
                        int totalPrc = 0;
                        for (Tenant item : value) {
                            Integer p = item.percentage().getValue();
                            if (p != null) {
                                totalPrc += p.intValue();
                            }
                        }
                        return (totalPrc == 100 ? null : new ValidationFailure(i18n.tr("Sum Of all Percentages should be equal to 100%!")));
                    } else {
                        return new ValidationFailure(i18n.tr("At least one Applicant should be present!"));
                    }
                }
                return null;
            }
        });
    }

    private class TenantInLeaseEditor extends CEntityDecoratableForm<Tenant> {

        public TenantInLeaseEditor() {
            super(Tenant.class);
        }

        @SuppressWarnings("unchecked")
        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            FormFlexPanel left = new FormFlexPanel();
            int row = -1;
            left.setWidget(++row, 0, inject(proto().customer().person().name(), new NameEditor(i18n.tr("Tenant"), Tenant.class) {
                @Override
                public Key getLinkKey() {
                    return TenantInLeaseEditor.this.getValue().getPrimaryKey();
                }
            }));
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().sex()), 7).build());
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().birthDate()), 9).build());

            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().role(), new CComboBox<LeaseParticipant.Role>()), 15).build());
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
                ((CComboBox<LeaseParticipant.Role>) get(proto().role())).addValueChangeHandler(new ValueChangeHandler<LeaseParticipant.Role>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<LeaseParticipant.Role> event) {
                        get(proto().relationship()).setVisible(event.getValue() != LeaseParticipant.Role.Applicant);
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
        protected void onPopulate() {
            super.onPopulate();

            get(proto().customer().person().email()).setMandatory(!getValue().customer().user().isNull());

            boolean applicant = (getValue().role().getValue() == LeaseParticipant.Role.Applicant);
            if (applicant) {
                get(proto().role()).setViewable(true);
                get(proto().relationship()).setVisible(false);
            } else if (isEditable()) {
                Collection<LeaseParticipant.Role> roles = EnumSet.allOf(LeaseParticipant.Role.class);
                if (getValue().role().getValue() != null) { // if not new entity creation...
                    roles.remove(LeaseParticipant.Role.Applicant);
                }
                ((CComboBox<LeaseParticipant.Role>) get(proto().role())).setOptions(roles);
            }

            if (!applicant && !getValue().customer().person().birthDate().isNull()) {
                if (!ValidationUtils.isOlderThen18(getValue().customer().person().birthDate().getValue())) {
                    setMandatoryDependant();
                }
            }

            if (get(proto().screening()) instanceof CEntityComboBox<?>) {
                CEntityComboBox<PersonScreening> combo = (CEntityComboBox<PersonScreening>) get(proto().screening());
                combo.resetCriteria();
                combo.addCriterion(PropertyCriterion.eq(combo.proto().screene(), getValue().customer()));
            }
        }

// TODO : implement percent recalculation logic         
//        @Override
//        protected void propagateValue(Tenant entity, boolean fireEvent, boolean populate) {
//            super.propagateValue(entity, fireEvent, populate);
//            if ((getValue().role().getValue() == Role.Applicant)) {
//                get(proto().percentage()).setEditable(false);
//                get(proto().percentage()).setViewable(true);
//            }
//        }

        @SuppressWarnings("unchecked")
        @Override
        public void addValidations() {
            CComponent<Integer, ?> prc = get(proto().percentage());
            if (prc instanceof CNumberField) {
                ((CNumberField<Integer>) prc).setRange(0, 100);
            }
        }

        private void setMandatoryDependant() {
            get(proto().role()).setValue(LeaseParticipant.Role.Dependent);
            get(proto().role()).setEditable(false);
        }
    }
}