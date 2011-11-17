/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.marketing.lead;

import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerBase.ItemSelectionHandler;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.client.ui.crud.building.SelectedBuildingLister;
import com.propertyvista.crm.client.ui.crud.floorplan.SelectedFloorplanLister;
import com.propertyvista.crm.client.ui.crud.marketing.inquiry.InquiryEditorView;
import com.propertyvista.crm.rpc.CrmSiteMap.Marketing;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lead.Lead;

public class LeadEditorViewImpl extends CrmEditorViewImplBase<Lead> implements LeadEditorView {

    private final IListerView<Building> buildingLister;

    private final IListerView<Floorplan> floorplanLister;

    public LeadEditorViewImpl() {
        super(Marketing.Lead.class, new LeadEditorForm());

        buildingLister = new ListerInternalViewImplBase<Building>(new SelectedBuildingLister(/* readOnly */));
        buildingLister.getLister().addItemSelectionHandler(new ItemSelectionHandler<Building>() {
            @Override
            public void onSelect(Building selectedItem) {
                ((InquiryEditorView.Presenter) presenter).setSelectedBuilding(selectedItem);
            }
        });
        floorplanLister = new ListerInternalViewImplBase<Floorplan>(new SelectedFloorplanLister(/* readOnly */));
    }

    @Override
    public IListerView<Building> getBuildingListerView() {
        return buildingLister;
    }

    @Override
    public IListerView<Floorplan> getFloorplanListerView() {
        return floorplanLister;
    }
}
