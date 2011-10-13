/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 13, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.board.BoardView;
import com.propertyvista.crm.client.ui.board.CrmBoardView;
import com.propertyvista.crm.rpc.services.SelectBuildingCrudService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public abstract class CrmBoardViewActivity<V extends BoardView> extends BoardViewActivity<V> {

    private IListerView.Presenter buildingsLister;

    public CrmBoardViewActivity(V view, Place place) {
        super(view, place);

    }

    @SuppressWarnings("unchecked")
    @Override
    public void onPopulateSuccess(DashboardMetadata result) {
        super.onPopulateSuccess(result);

        buildingsLister = null;
        if (view instanceof CrmBoardView && ((CrmBoardView) view).getBuildingListerView() != null) {
            buildingsLister = new ListerActivityBase<Building>(((CrmBoardView) view).getBuildingListerView(),
                    (AbstractCrudService<Building>) GWT.create(SelectBuildingCrudService.class), Building.class);
            buildingsLister.populate(0);
        }
    }
}