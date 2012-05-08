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

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.LeaseDTO;

public class GuarantorInLeaseFolder extends LeaseParticipantFolder<Guarantor> {

    static final I18n i18n = I18n.get(GuarantorInLeaseFolder.class);

    private final CEntityForm<? extends LeaseDTO> lease;

    public GuarantorInLeaseFolder(CEntityForm<? extends LeaseDTO> parent, boolean modifiable) {
        super(Guarantor.class, modifiable);
        this.lease = parent;
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
        for (Customer tenant : customers) {
            Guarantor newGuarantorInLease = EntityFactory.create(Guarantor.class);
            newGuarantorInLease.leaseV().setPrimaryKey(lease.getValue().version().getPrimaryKey());
            newGuarantorInLease.customer().set(tenant);
            newGuarantorInLease.role().setValue(LeaseParticipant.Role.Guarantor);
            newGuarantorInLease.relationship().setValue(PersonRelationship.Other); // just not leave it empty - it's mandatory field!
            addItem(newGuarantorInLease);
        }
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof Guarantor) {
            return new GuarantorInLeaseEditor();
        }
        return super.create(member);
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
            left.setWidget(++row, 0, inject(proto().customer().person().name(), new NameEditor(i18n.tr("Guarantor"), Guarantor.class) {
                @Override
                public Key getLinkKey() {
                    return GuarantorInLeaseEditor.this.getValue().getPrimaryKey();
                }
            }));
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().sex()), 7).build());
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().birthDate()), 9).build());
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().relationship()), 15).build());
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().screening()), 9).customLabel(i18n.tr("Use Screening From")).build());
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().tenant(), new CComboBox<Tenant>() {
                @Override
                public String getItemName(Tenant o) {
                    if (o == null) {
                        return getNoSelectionText();
                    } else {
                        return o.getStringView();
                    }
                }
            }), 25).customLabel(i18n.tr("Referred by Tenant")).build());

            FormFlexPanel right = new FormFlexPanel();
            row = -1;
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().email()), 25).build());
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().homePhone()), 15).build());
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().mobilePhone()), 15).build());
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().workPhone()), 15).build());

            // assemble main panel:
            main.setWidget(0, 0, left);
            main.setWidget(0, 1, right);

            main.getColumnFormatter().setWidth(0, "60%");
            main.getColumnFormatter().setWidth(1, "40%");

            return main;
        }

        @Override
        protected void onPopulate() {
            super.onPopulate();

            get(proto().customer().person().email()).setMandatory(!getValue().customer().user().isNull());

            if (get(proto().screening()) instanceof CEntityComboBox<?>) {
                @SuppressWarnings("unchecked")
                CEntityComboBox<PersonScreening> combo = (CEntityComboBox<PersonScreening>) get(proto().screening());
                combo.resetCriteria();
                combo.addCriterion(PropertyCriterion.eq(combo.proto().screene(), getValue().customer()));
            }

            if (get(proto().tenant()) instanceof CComboBox<?>) {
                @SuppressWarnings("unchecked")
                CComboBox<Tenant> combo = (CComboBox<Tenant>) get(proto().tenant());
                combo.setOptions(lease.getValue().version().tenants());
                combo.getOptions();
            }
        }
    }
}