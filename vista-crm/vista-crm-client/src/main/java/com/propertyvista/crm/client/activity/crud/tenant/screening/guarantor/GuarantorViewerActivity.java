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
package com.propertyvista.crm.client.activity.crud.tenant.screening.guarantor;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView.Presenter;

import com.propertyvista.crm.client.ui.crud.tenant.screening.guarantor.GuarantorViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.TenantViewFactory;
import com.propertyvista.crm.rpc.services.GuarantorCrudService;
import com.propertyvista.crm.rpc.services.PersonScreeningCrudService;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.dto.GuarantorDTO;

public class GuarantorViewerActivity extends ViewerActivityBase<GuarantorDTO> implements GuarantorViewerView.Presenter {

    private final IListerView.Presenter screeningLister;

    @SuppressWarnings("unchecked")
    public GuarantorViewerActivity(Place place) {
        super(place, TenantViewFactory.instance(GuarantorViewerView.class), (AbstractCrudService<GuarantorDTO>) GWT.create(GuarantorCrudService.class));

        screeningLister = new ListerActivityBase<PersonScreening>(place, ((GuarantorViewerView) view).getScreeningListerView(),
                (AbstractCrudService<PersonScreening>) GWT.create(PersonScreeningCrudService.class), PersonScreening.class);

    }

    @Override
    public Presenter getScreeningPresenter() {
        return screeningLister;
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
