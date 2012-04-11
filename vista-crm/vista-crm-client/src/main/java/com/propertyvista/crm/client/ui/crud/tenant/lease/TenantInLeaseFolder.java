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
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.components.boxes.CustomerSelectorDialog;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.Tenant.Role;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.util.ValidationUtils;

class TenantInLeaseFolder extends VistaBoxFolder<Tenant> {

    static final I18n i18n = I18n.get(TenantInLeaseFolder.class);

    private final CEntityEditor<? extends Lease> parent;

    private final LeaseEditorView view;

    public TenantInLeaseFolder(CEntityEditor<? extends Lease> parent) {
        this(parent, null); // view mode constructor
    }

    public TenantInLeaseFolder(CEntityEditor<? extends Lease> parent, LeaseEditorView view) {
        super(Tenant.class, parent.isEditable());
        this.parent = parent;
        this.view = view;
        setOrderable(false);
    }

    @Override
    protected void addItem() {
        new CustomerSelectorDialog(extractTenantFromTenantInLeaseList(getValue())) {
            @Override
            public boolean onClickOk() {
                if (getSelectedItems().isEmpty()) {
                    return false;
                } else {
                    for (Customer tenant : getSelectedItems()) {
                        Tenant newTenantInLease = EntityFactory.create(Tenant.class);
                        newTenantInLease.leaseV().setPrimaryKey(parent.getValue().version().getPrimaryKey());
                        newTenantInLease.customer().set(tenant);
                        if (!isApplicantPresent()) {
                            newTenantInLease.role().setValue(Role.Applicant);
                            newTenantInLease.relationship().setValue(PersonRelationship.Other); // just not leave it empty - it's mandatory field!
                        }
                        addItem(newTenantInLease);
                    }
                    return true;
                }
            }
        }.show();
    }

    private boolean isApplicantPresent() {
        for (Tenant til : getValue()) {
            if (Role.Applicant == til.role().getValue()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof Tenant) {
            return new TenantInLeaseEditor();
        }
        return super.create(member);
    }

    private class TenantInLeaseEditor extends CEntityDecoratableEditor<Tenant> {

        private boolean applicant;

        public TenantInLeaseEditor() {
            super(Tenant.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            int row = -1;
            if (isEditable()) {
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().name().namePrefix()), 5).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().name().firstName()), 15).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().name().middleName()), 5).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().name().lastName()), 25).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().name().maidenName()), 25).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().name().nameSuffix()), 5).build());
            } else {
                main.setWidget(++row, 0,
                        new DecoratorBuilder(inject(proto().customer().person().name(), new CEntityLabel<Name>()), 25).customLabel(i18n.tr("Tenant")).build());
                get(proto().customer().person().name()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
            }
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().sex()), 7).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().birthDate()), 9).build());

            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().role()), 15).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().relationship()), 15).build());

            row = -1; // second column
            main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().customer().person().homePhone()), 15).build());
            main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().customer().person().mobilePhone()), 15).build());
            main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().customer().person().workPhone()), 15).build());
            main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().customer().person().email()), 25).build());

            main.getColumnFormatter().setWidth(0, "60%");
            main.getColumnFormatter().setWidth(1, "40%");
            return main;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPopulate() {
            super.onPopulate();

            applicant = (getValue().role().getValue() == Role.Applicant);
            if (applicant) {
                get(proto().role()).setViewable(true);
                get(proto().relationship()).setVisible(false);
            } else if (get(proto().role()) instanceof CComboBox) {
                Collection<Tenant.Role> roles = EnumSet.allOf(Tenant.Role.class);
                roles.remove(Tenant.Role.Applicant);
                ((CComboBox<Role>) get(proto().role())).setOptions(roles);
            }

            if (!applicant && !getValue().customer().person().birthDate().isNull()) {
                if (!ValidationUtils.isOlderThen18(getValue().customer().person().birthDate().getValue())) {
                    setMandatoryDependant();
                }
            }
        }

        private void setMandatoryDependant() {
            get(proto().role()).setValue(Tenant.Role.Dependent);
            get(proto().role()).setEditable(false);
        }
    }

    private static List<Customer> extractTenantFromTenantInLeaseList(List<Tenant> list) {
        List<Customer> tenants = new ArrayList<Customer>(list.size());
        for (Tenant wrapper : list) {
            tenants.add(wrapper.customer());
        }
        return tenants;
    }
}