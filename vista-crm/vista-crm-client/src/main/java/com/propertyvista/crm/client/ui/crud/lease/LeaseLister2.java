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
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.tenant.lease.Lease2;
import com.propertyvista.domain.tenant.lease.Lease2.PaymentFrequency;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.dto.LeaseDTO2;
import com.propertyvista.dto.LeaseTermDTO;

public class LeaseLister2 extends ListerBase<LeaseDTO2> {

    private final static I18n i18n = I18n.get(LeaseLister2.class);

    public LeaseLister2() {
        super(LeaseDTO2.class, true);
        getDataTablePanel().getAddButton().setCaption(i18n.tr("Add Existing Lease"));

        setColumnDescriptors(//@formatter:off
            new Builder(proto().leaseId()).build(),
            new Builder(proto().type()).build(),
            
            new Builder(proto().unit().building().propertyCode()).build(),
            new Builder(proto().unit()).build(),
            
            new Builder(proto().status()).build(),
            new Builder(proto().completion()).build(),
// TODO _2 uncomment then
//            new Builder(proto().billingAccount().accountNumber()).build(),
            
            new Builder(proto().leaseFrom()).build(),
            new Builder(proto().leaseTo()).build(),
            
            new Builder(proto().expectedMoveIn()).build(),
            new Builder(proto().expectedMoveOut(), false).build(),
            new Builder(proto().actualMoveIn(), false).build(),
            new Builder(proto().actualMoveOut(), false).build(),
            new Builder(proto().moveOutNotice(), false).build(),
            
            new Builder(proto().approvalDate(), false).build(),
            new Builder(proto().creationDate(), false).build()
        );//@formatter:on
    }

    @Override
    protected void onItemNew() {
        new ExistingLeaseDataDialog().show();
    }

    private LeaseDTO2 createNewLease(Service.ServiceType leaseType, BigDecimal balance) {
        LeaseDTO2 newLease = EntityFactory.create(LeaseDTO2.class);

        newLease.type().setValue(leaseType);
        newLease.paymentFrequency().setValue(PaymentFrequency.Monthly);
        newLease.status().setValue(Lease2.Status.Created);
// TODO 2 uncomment then
//        newLease.billingAccount().carryforwardBalance().setValue(balance);

        return newLease;
    }

    private class ExistingLeaseDataDialog extends SelectEnumDialog<Service.ServiceType> {

        private CMoneyField balance;

        public ExistingLeaseDataDialog() {
            super(i18n.tr("Lease Data"), EnumSet.allOf(Service.ServiceType.class));
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
                public ValidationError isValid(CComponent<BigDecimal, ?> component, BigDecimal value) {
                    return (value == null ? new ValidationError(component, i18n.tr("Initial balance value shoud be set!")) : null);
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
                // prepare LeaseTermDTO:
                LeaseTermDTO termDto = EntityFactory.create(LeaseTermDTO.class);
                termDto.newParentLease().set(createNewLease(getSelectedType(), balance.getValue()));
                termDto.type().setValue(LeaseTerm.Type.FixedEx);
                termDto.lease().set(termDto.newParentLease());

                AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.LeaseTerm().formNewItemPlace(termDto));
                return true;
            }
            return false;
        }
    }
}
