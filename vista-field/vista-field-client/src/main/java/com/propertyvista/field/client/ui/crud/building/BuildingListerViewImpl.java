/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 1, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.field.client.ui.crud.building;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.PageOrientation;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister.ItemSelectionHandler;
import com.pyx4j.site.client.ui.prime.lister.ListerViewImplBase;

import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.field.client.FieldSite;
import com.propertyvista.field.client.event.ChangeHeaderEvent;
import com.propertyvista.field.client.event.ChangeLayoutEvent;
import com.propertyvista.field.client.event.HeaderAction;
import com.propertyvista.field.client.event.LayoutAction;

public class BuildingListerViewImpl extends ListerViewImplBase<BuildingDTO> implements BuildingListerView {

    public BuildingListerViewImpl() {
        BuildingLister lister = new BuildingLister();
        lister.addItemSelectionHandler(new ItemSelectionHandler<BuildingDTO>() {

            @Override
            public void onSelect(BuildingDTO selectedItem) {
                if (FieldSite.getPageOrientation() == PageOrientation.Vertical) {
                    AppSite.getEventBus().fireEvent(new ChangeHeaderEvent(HeaderAction.ShowNavigDetails));
                    AppSite.getEventBus().fireEvent(new ChangeLayoutEvent(LayoutAction.ExpandDetails));
                }
            }

        });

        setLister(lister);
    }

}
