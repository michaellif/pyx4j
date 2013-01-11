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

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;

import com.propertyvista.crm.client.activity.crud.lease.common.LeaseTermEditorActivity;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.dto.LeaseTermDTO;

public class LeaseDataDialog extends SelectEnumDialog<Service.ServiceType> implements OkCancelOption {

    private final static I18n i18n = I18n.get(LeaseDataDialog.class);

    Type type;

    private final AptUnit selectedUnitId;

    public enum Type {
        New, Current, Application
    }

    public LeaseDataDialog(Type type) {
        this(type, null);
    }

    public LeaseDataDialog(Type type, AptUnit selectedUnitId) {
        super(i18n.tr("Select Lease Type"), Service.ServiceType.unitRelated());
        this.type = type;
        this.selectedUnitId = selectedUnitId;
    }

    @Override
    public boolean onClickOk() {
        // prepare LeaseTermDTO:
        LeaseTermDTO termDto = EntityFactory.create(LeaseTermDTO.class);

        termDto.newParentLease().set(createNewLease(getSelectedType()));
        termDto.newParentLease().currentTerm().set(termDto);

        termDto.type().setValue(LeaseTerm.Type.FixedEx);
        termDto.status().setValue(LeaseTerm.Status.Current);
        termDto.lease().set(termDto.newParentLease());

        switch (type) {
        case New:
        case Current:
            AppSite.getPlaceController().goTo(
                    new CrmSiteMap.Tenants.LeaseTerm().formNewItemPlace(termDto).queryArg(LeaseTermEditorActivity.ARG_NAME_RETURN_BH,
                            LeaseTermEditorActivity.ReturnBehaviour.Lease.name()));
            break;

        case Application:
            AppSite.getPlaceController().goTo(
                    new CrmSiteMap.Tenants.LeaseTerm().formNewItemPlace(termDto).queryArg(LeaseTermEditorActivity.ARG_NAME_RETURN_BH,
                            LeaseTermEditorActivity.ReturnBehaviour.Application.name()));
            break;
        }
        return true;
    }

    protected Lease createNewLease(Service.ServiceType leaseType) {
        Lease newLease = EntityFactory.create(Lease.class);

        newLease.type().setValue(leaseType);
        newLease.paymentFrequency().setValue(PaymentFrequency.Monthly);

        newLease.unit().set(selectedUnitId);

        switch (type) {
        case New:
            newLease.status().setValue(Lease.Status.NewLease);
            break;

        case Current:
            newLease.status().setValue(Lease.Status.ExistingLease);
            newLease.billingAccount().carryforwardBalance().setValue(BigDecimal.ZERO);
            break;

        case Application:
            newLease.status().setValue(Lease.Status.Application);
            break;
        }

        return newLease;
    }
}
