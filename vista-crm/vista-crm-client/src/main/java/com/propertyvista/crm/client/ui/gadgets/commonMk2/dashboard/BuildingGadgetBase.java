/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 12, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard;

import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public abstract class BuildingGadgetBase<G extends GadgetMetadata> extends GadgetInstanceBase<G> {

    protected IBuildingFilterContainer buildingsFilterContainer;

    public BuildingGadgetBase(GadgetMetadata metadata, Class<G> metadataClass) {
        super(metadata, metadataClass);
    }

    @Override
    public void setContainerBoard(IBuildingFilterContainer board) {
        this.buildingsFilterContainer = board;
        this.buildingsFilterContainer.addBuildingSelectionChangedEventHandler(new BuildingSelectionChangedEventHandler() {
            @Override
            public void onBuildingSelectionChanged(BuildingSelectionChangedEvent event) {
                populate();
            }
        });
    }

}
