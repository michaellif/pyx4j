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
package com.propertyvista.crm.client.activity.crud.lease.guarantor;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView.Presenter;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ui.components.security.PasswordChangeView;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.lease.guarantor.GuarantorViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.TenantViewFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.tenant.screening.GuarantorCrudService;
import com.propertyvista.crm.rpc.services.tenant.screening.PersonScreeningCrudService;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.dto.GuarantorDTO;

public class GuarantorViewerActivity extends CrmViewerActivity<GuarantorDTO> implements GuarantorViewerView.Presenter {

    private final IListerView.Presenter<PersonScreening> screeningLister;

    @SuppressWarnings("unchecked")
    public GuarantorViewerActivity(Place place) {
        super(place, TenantViewFactory.instance(GuarantorViewerView.class), (AbstractCrudService<GuarantorDTO>) GWT.create(GuarantorCrudService.class));

        screeningLister = new ListerActivityBase<PersonScreening>(place, ((GuarantorViewerView) view).getScreeningListerView(),
                (AbstractCrudService<PersonScreening>) GWT.create(PersonScreeningCrudService.class), PersonScreening.class);

    }

    @Override
    public Presenter<PersonScreening> getScreeningListerPresenter() {
        return screeningLister;
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
    public void onPopulateSuccess(GuarantorDTO result) {
        super.onPopulateSuccess(result);

        screeningLister.setParent(result.getPrimaryKey(), Guarantor.class);
        screeningLister.populate();
    }

    @Override
    public void onStop() {
        ((AbstractActivity) screeningLister).onStop();
        super.onStop();
    }
}
