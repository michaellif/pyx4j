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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionModel;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CMoneyField;
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
            
            new Builder(proto().leaseFrom()).build(),
            new Builder(proto().leaseTo()).build(),
            
            new Builder(proto().version().expectedMoveIn()).build(),
            new Builder(proto().version().expectedMoveOut(), false).build(),
            new Builder(proto().version().actualMoveIn(), false).build(),
            new Builder(proto().version().actualMoveOut(), false).build(),
            new Builder(proto().version().moveOutNotice(), false).build(),
            
            new Builder(proto().approvalDate(), false).build(),
            new Builder(proto().createDate(), false).build(),
            
            new Builder(proto().version().tenants()).build()
        );//@formatter:on
    }

    @Override
    protected EntityListCriteria<LeaseDTO> updateCriteria(EntityListCriteria<LeaseDTO> criteria) {

        switch (getVersionDisplayMode()) {
        case displayDraft:
            criteria.setVersionedCriteria(VersionedCriteria.onlyDraft);
            criteria.add(PropertyCriterion.in(criteria.proto().version().status(), Lease.Status.currentNew()));
            break;
        case displayFinal:
            criteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
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
        newLease.billingAccount().initialBalance().setValue(balance);
        newLease.version().status().setValue(Lease.Status.Created);
        return newLease;
    }

    private class ExistingLeaseDataDialog extends SelectEnumDialog<Service.Type> implements OkCancelOption {

        private CMoneyField balance;

        public ExistingLeaseDataDialog() {
            super(i18n.tr("Enter Lease Data"), EnumSet.allOf(Service.Type.class));
            getOkButton().setEnabled(false);
        }

        @Override
        protected <E extends Enum<E>> Widget initBody(SelectionModel<E> selectionModel, EnumSet<E> values, String height) {
            this.balance = new CMoneyField();
            this.balance.addValueChangeHandler(new ValueChangeHandler<BigDecimal>() {
                @Override
                public void onValueChange(ValueChangeEvent<BigDecimal> event) {
                    getOkButton().setEnabled(event.getValue() != null);
                }
            });

            VerticalPanel body = new VerticalPanel();

            body.add(new HTML(i18n.tr("Lease Type:")));
            body.add(super.initBody(selectionModel, values, height));

            HorizontalPanel balance = new HorizontalPanel();
            balance.add(new HTML(i18n.tr("Initial balance:")));
            balance.add(this.balance.asWidget());
            balance.setCellWidth(this.balance.asWidget(), "100px");
            balance.getElement().getStyle().setPaddingTop(6, Unit.PX);
            balance.getElement().getStyle().setPaddingBottom(2, Unit.PX);
            balance.setWidth("100%");
            body.add(balance);

            body.setWidth("100%");
            return body;
        }

        @Override
        public boolean onClickOk() {
            getPresenter().editNew(getItemOpenPlaceClass(), createNewLease(getSelectedType(), balance.getValue()));
            return true;
        }

        @Override
        public boolean onClickCancel() {
            return true;
        };

        @Override
        public String defineHeight() {
            return "80px";
        };

        @Override
        public String defineWidth() {
            return "300px";
        }
    }
}
