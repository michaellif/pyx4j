/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-08-17
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common.dialogs;

import java.math.BigDecimal;
import java.util.EnumSet;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionModel;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.crm.client.activity.crud.lease.common.LeaseTermEditorActivity;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.dto.LeaseTermDTO;

public class ExistingLeaseDataDialog extends SelectEnumDialog<Service.ServiceType> {

    private final static I18n i18n = I18n.get(ExistingLeaseDataDialog.class);

    private CMoneyField balance;

    private final AptUnit selectedUnitId;

    public ExistingLeaseDataDialog() {
        this(null);
    }

    public ExistingLeaseDataDialog(AptUnit selectedUnitId) {
        super(i18n.tr("Lease Data"), EnumSet.allOf(Service.ServiceType.class));
        this.selectedUnitId = selectedUnitId;
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
            termDto.newParentLease().currentTerm().set(termDto);

            termDto.type().setValue(LeaseTerm.Type.FixedEx);
            termDto.lease().set(termDto.newParentLease());

            AppSite.getPlaceController().goTo(
                    new CrmSiteMap.Tenants.LeaseTerm().formNewItemPlace(termDto).queryArg(LeaseTermEditorActivity.ARG_NAME_RETURN_BH,
                            LeaseTermEditorActivity.ReturnBehaviour.Lease.name()));
            return true;
        }
        return false;
    }

    private Lease createNewLease(Service.ServiceType leaseType, BigDecimal balance) {
        Lease newLease = EntityFactory.create(Lease.class);

        newLease.type().setValue(leaseType);
        newLease.paymentFrequency().setValue(PaymentFrequency.Monthly);
        newLease.status().setValue(Lease.Status.ExistingLease);

        newLease.billingAccount().carryforwardBalance().setValue(balance);

        newLease.unit().set(selectedUnitId);

        return newLease;
    }
}
