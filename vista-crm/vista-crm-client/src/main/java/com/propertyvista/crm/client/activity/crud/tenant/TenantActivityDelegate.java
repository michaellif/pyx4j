/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-28
 * @author TPRGLET
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.tenant;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.IListerView.Presenter;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.tenant.TenantView;
import com.propertyvista.crm.rpc.services.TenantScreeningCrudService;
import com.propertyvista.domain.tenant.TenantScreening;

public class TenantActivityDelegate implements TenantView.Presenter {

    private final IListerView.Presenter screeningLister;

    @SuppressWarnings("unchecked")
    public TenantActivityDelegate(TenantView view) {

        screeningLister = new ListerActivityBase<TenantScreening>(view.getScreeningListerView(),
                (AbstractCrudService<TenantScreening>) GWT.create(TenantScreeningCrudService.class), TenantScreening.class);
    }

    @Override
    public Presenter getScreeningPresenter() {
        return screeningLister;
    }

    public void populate(Key parentID) {
        screeningLister.setParentFiltering(parentID);
        screeningLister.populate(0);
    }
}
