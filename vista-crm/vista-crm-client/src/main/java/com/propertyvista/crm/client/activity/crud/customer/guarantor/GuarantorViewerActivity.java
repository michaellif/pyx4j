/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.customer.guarantor;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.common.client.ui.components.security.PasswordChangeView;
import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.customer.guarantor.GuarantorViewerView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.customer.GuarantorCrudService;
import com.propertyvista.crm.rpc.services.customer.screening.LeaseParticipantScreeningCrudService;
import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.GuarantorDTO;

public class GuarantorViewerActivity extends CrmViewerActivity<GuarantorDTO> implements GuarantorViewerView.Presenter {

    private LeaseParticipant<?> leaseParticipantId;

    private GuarantorDTO currentValue;

    public GuarantorViewerActivity(CrudAppPlace place) {
        super(GuarantorDTO.class, place, CrmSite.getViewFactory().getView(GuarantorViewerView.class), GWT
                .<GuarantorCrudService> create(GuarantorCrudService.class));
    }

    @Override
    public void onPopulateSuccess(GuarantorDTO result) {
        super.onPopulateSuccess(result);
    
        currentValue = result;
    
        leaseParticipantId = EntityFactory.createIdentityStub(Guarantor.class, result.getPrimaryKey());
    }

    @Override
    public void goToCreateScreening() {
        LeaseParticipantScreeningCrudService.CustomerScreeningInitializationData id = EntityFactory
                .create(LeaseParticipantScreeningCrudService.CustomerScreeningInitializationData.class);
        id.leaseParticipantId().set(leaseParticipantId);
        AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.Screening().formNewItemPlace(id));
    }

    @Override
    public void goToChangePassword(Key guarantorPrincipalPk, String guarantorName) {
        if (guarantorPrincipalPk != null) {
            AppPlace passwordChangePlace = new CrmSiteMap.PasswordChange();
            passwordChangePlace.queryArg(PasswordChangeView.Presenter.PRINCIPAL_PK_ARG, guarantorPrincipalPk.toString());
            passwordChangePlace.queryArg(PasswordChangeView.Presenter.PRINCIPAL_NAME_ARG, guarantorName);
            passwordChangePlace.queryArg(PasswordChangeView.Presenter.PRINCIPAL_CLASS, PasswordChangeView.Presenter.PrincipalClass.GUARANTOR.toString());
            AppSite.getPlaceController().goTo(passwordChangePlace);
        }
    }

    @Override
    public void viewScreening() {
        AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.Screening().formViewerPlace(currentValue.screening().getPrimaryKey()));
    }

}
