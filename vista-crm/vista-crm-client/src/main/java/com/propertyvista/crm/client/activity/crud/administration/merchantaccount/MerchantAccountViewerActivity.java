/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 12, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.administration.merchantaccount;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.site.client.ui.prime.lister.ILister.Presenter;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.ListerControllerFactory;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.administration.merchantaccount.MerchantAccountViewerView;
import com.propertyvista.crm.rpc.services.admin.MerchantAccountCrudService;
import com.propertyvista.crm.rpc.services.selections.SelectBuildingListService;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.VistaCrmBehavior;

public class MerchantAccountViewerActivity extends CrmViewerActivity<MerchantAccount> {

    private final Presenter<Building> buildingLister;

    public MerchantAccountViewerActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().getView(MerchantAccountViewerView.class), GWT
                .<AbstractCrudService<MerchantAccount>> create(MerchantAccountCrudService.class));

        buildingLister = ListerControllerFactory.create(((MerchantAccountViewerView) getView()).getBuildingListerView(),
                GWT.<AbstractListService<Building>> create(SelectBuildingListService.class), Building.class, VistaCrmBehavior.PropertyManagement);
    }

    @Override
    protected void onPopulateSuccess(MerchantAccount result) {
        super.onPopulateSuccess(result);

        buildingLister.clearPreDefinedFilters();
        buildingLister.addPreDefinedFilter(PropertyCriterion.eq(EntityFactory.getEntityPrototype(Building.class).merchantAccounts().$().merchantAccount(),
                result));
        buildingLister.populate();
    }
}
