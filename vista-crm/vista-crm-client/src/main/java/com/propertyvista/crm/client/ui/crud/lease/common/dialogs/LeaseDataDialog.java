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

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;

import com.propertyvista.crm.client.activity.crud.lease.common.LeaseTermEditorActivity;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.lease.common.LeaseTermCrudService;
import com.propertyvista.crm.rpc.services.lease.common.LeaseTermCrudService.LeaseTermInitializationData;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;

public class LeaseDataDialog extends SelectEnumDialog<ARCode.Type> implements OkCancelOption {

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
        super(i18n.tr("Select Lease Type"), ARCode.Type.unitRelatedServices());
        this.type = type;
        this.selectedUnitId = selectedUnitId;
    }

    @Override
    public boolean onClickOk() {
        switch (type) {
        case Current:
            AppSite.getPlaceController().goTo(
                    new CrmSiteMap.Tenants.LeaseTerm().formNewItemPlace(createInitData(Lease.Status.ExistingLease)).queryArg(
                            LeaseTermEditorActivity.ARG_NAME_RETURN_BH, LeaseTermEditorActivity.ReturnBehaviour.Lease.name()));
            break;

        case New:
            AppSite.getPlaceController().goTo(
                    new CrmSiteMap.Tenants.LeaseTerm().formNewItemPlace(createInitData(Lease.Status.NewLease)).queryArg(
                            LeaseTermEditorActivity.ARG_NAME_RETURN_BH, LeaseTermEditorActivity.ReturnBehaviour.Lease.name()));
            break;

        case Application:
            AppSite.getPlaceController().goTo(
                    new CrmSiteMap.Tenants.LeaseTerm().formNewItemPlace(createInitData(Lease.Status.Application)).queryArg(
                            LeaseTermEditorActivity.ARG_NAME_RETURN_BH, LeaseTermEditorActivity.ReturnBehaviour.Application.name()));
            break;
        }

        return true;
    }

    private LeaseTermInitializationData createInitData(final Lease.Status leaseStatus) {
        LeaseTermCrudService.LeaseTermInitializationData id = EntityFactory.create(LeaseTermCrudService.LeaseTermInitializationData.class);

        id.isOffer().setValue(false); // not an offer.
        id.leaseType().setValue(getSelectedType());
        id.leaseStatus().setValue(leaseStatus);
        id.unit().set(selectedUnitId);

        return id;
    }
}
