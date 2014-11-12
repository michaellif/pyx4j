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
package com.propertyvista.crm.client.ui.crud.administration.merchantaccount;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.PropertyCriterion;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.property.asset.building.Building;

public class MerchantAccountViewerViewImpl extends CrmViewerViewImplBase<MerchantAccount> implements MerchantAccountViewerView {

    private final BuildingLister buildingLister;

    public MerchantAccountViewerViewImpl() {
        buildingLister = new BuildingLister();

        setForm(new MerchantAccountForm(this));
    }

    @Override
    public void populate(MerchantAccount value) {
        buildingLister.getDataSource().clearPreDefinedFilters();
        buildingLister.getDataSource().addPreDefinedFilter(
                PropertyCriterion.eq(EntityFactory.getEntityPrototype(Building.class).merchantAccounts().$().merchantAccount(), value));
        buildingLister.populate();
    }

    @Override
    public BuildingLister getBuildingListerView() {
        return buildingLister;
    }
}
