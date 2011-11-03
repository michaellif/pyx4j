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
package com.propertyvista.crm.client.ui.crud.marketing.lead;

import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerBase.ItemSelectionHandler;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.client.ui.crud.building.SelectedBuildingLister;
import com.propertyvista.crm.client.ui.crud.unit.SelectedUnitLister;
import com.propertyvista.crm.rpc.CrmSiteMap.Marketing;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lead.Showing;

public class ShowingEditorViewImpl extends CrmEditorViewImplBase<Showing> implements ShowingEditorView {

    private final IListerView<Building> buildingLister;

    private final IListerView<AptUnit> unitLister;

    public ShowingEditorViewImpl() {
        super(Marketing.Showing.class);

        buildingLister = new ListerInternalViewImplBase<Building>(new SelectedBuildingLister(/* readOnly */));
        buildingLister.getLister().addItemSelectionHandler(new ItemSelectionHandler<Building>() {
            @Override
            public void onSelect(Building selectedItem) {
                ((ShowingEditorView.Presenter) presenter).setSelectedBuilding(selectedItem);
            }
        });
        unitLister = new ListerInternalViewImplBase<AptUnit>(new SelectedUnitLister(/* readOnly */));

        // set main form here: 
        setForm(new ShowingEditorForm());
    }

    @Override
    public IListerView<Building> getBuildingListerView() {
        return buildingLister;
    }

    @Override
    public IListerView<AptUnit> getUnitListerView() {
        return unitLister;
    }
}
