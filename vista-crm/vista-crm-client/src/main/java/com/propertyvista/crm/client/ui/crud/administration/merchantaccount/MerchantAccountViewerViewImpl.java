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

import com.pyx4j.site.client.ui.prime.lister.ILister;
import com.pyx4j.site.client.ui.prime.lister.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.property.asset.building.Building;

public class MerchantAccountViewerViewImpl extends CrmViewerViewImplBase<MerchantAccount> implements MerchantAccountViewerView {

    private final ILister<Building> buildingLister;

    public MerchantAccountViewerViewImpl() {

        buildingLister = new ListerInternalViewImplBase<Building>(new BuildingLister());

        setForm(new MerchantAccountForm(this));
    }

    @Override
    public ILister<Building> getBuildingListerView() {
        return buildingLister;
    }
}
