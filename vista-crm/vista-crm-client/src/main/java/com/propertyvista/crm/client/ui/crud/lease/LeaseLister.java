/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease;

import java.math.BigDecimal;
import java.util.EnumSet;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionModel;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;

import com.propertyvista.common.client.ui.components.VersionedLister;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.dto.LeaseDTO;

public class LeaseLister extends VersionedLister<LeaseDTO> {

    private final static I18n i18n = I18n.get(LeaseLister.class);

    public LeaseLister() {
        super(LeaseDTO.class, false, true);

        setColumnDescriptors(//@formatter:off
            new Builder(proto().leaseId()).build(),
            new Builder(proto().type()).build(),
            
            new Builder(proto().unit().belongsTo().propertyCode()).build(),
            new Builder(proto().unit()).build(),
            
            new Builder(proto().version().status()).build(),
            new Builder(proto().version().completion()).build(),

            new Builder(proto().billingAccount().accountNumber()).build(),
            
            new Builder(proto().leaseFrom()).build(),
            new Builder(proto().leaseTo()).build(),
            
            new Builder(proto().version().expectedMoveIn()).build(),
            new Builder(proto().version().expectedMoveOut(), false).build(),
            new Builder(proto().version().actualMoveIn(), false).build(),
            new Builder(proto().version().actualMoveOut(), false).build(),
            new Builder(proto().version().moveOutNotice(), false).build(),
            
            new Builder(proto().approvalDate(), false).build(),
            new Builder(proto().creationDate(), false).build(),
            
            new Builder(proto().version().tenants()).build()
        );//@formatter:on
    }

    @Override
    protected EntityListCriteria<LeaseDTO> updateCriteria(EntityListCriteria<LeaseDTO> criteria) {
        switch (getVersionDisplayMode()) {
        case displayDraft:
            criteria.add(PropertyCriterion.in(criteria.proto().version().status(), Lease.Status.currentNew()));
            break;
        case displayFinal:
            criteria.add(PropertyCriterion.in(criteria.proto().version().status(), Lease.Status.current()));
            break;
        }
        return super.updateCriteria(criteria);
    }

    @Override
    protected void onItemNew() {
        new ExistingLeaseDataDialog().show();
    }

    private LeaseDTO createNewLease(Service.Type leaseType, BigDecimal balance) {
        LeaseDTO newLease = EntityFactory.create(LeaseDTO.class);
        newLease.type().setValue(leaseType);
        newLease.paymentFrequency().setValue(PaymentFrequency.Monthly);
        newLease.billingAccount().carryforwardBalance().setValue(balance);
        newLease.version().status().setValue(Lease.Status.Created);
        return newLease;
    }

    private class ExistingLeaseDataDialog extends SelectEnumDialog<Service.Type> implements OkCancelOption {

        private CMoneyField balance;

        public ExistingLeaseDataDialog() {
            super(i18n.tr("Lease Data"), EnumSet.allOf(Service.Type.class));
        }

        @Override
        protected <E extends Enum<E>> Widget initBody(SelectionModel<E> selectionModel, EnumSet<E> values, String height) {
            VerticalPanel body = new VerticalPanel();

            body.add(new HTML("<b>" + i18n.tr("Type") + ":</b>"));
            body.add(super.initBody(selectionModel, values, height));

            balance = new CMoneyField();
            balance.setTitle(i18n.tr("Initial balance"));
            balance.addValueValidator(new EditableValueValidator<BigDecimal>() {
                @Override
                public ValidationFailure isValid(CComponent<BigDecimal, ?> component, BigDecimal value) {
                    return (value == null ? new ValidationFailure(i18n.tr("Initial balance value shoud be entered!")) : null);
                }
            });
            Widget w;
            body.add(w = new WidgetDecorator.Builder(balance).componentWidth(7).build());
            w.getElement().getStyle().setPaddingTop(6, Unit.PX);

            body.setWidth("100%");
            return body;
        }

        @Override
        public String defineWidth() {
            return "25em";
        }

        @Override
        public boolean onClickOk() {
            balance.setVisited(true);
            if (balance.isValid()) {
                getPresenter().editNew(getItemOpenPlaceClass(), createNewLease(getSelectedType(), balance.getValue()));
                return true;
            }
            return false;
        }

        @Override
        public boolean onClickCancel() {
            return true;
        };
    }
}
