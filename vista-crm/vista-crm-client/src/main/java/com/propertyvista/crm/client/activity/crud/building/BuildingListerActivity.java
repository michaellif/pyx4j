/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-03
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.building;

import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.backoffice.activity.AbstractListerActivity;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.building.BuildingListerView;
import com.propertyvista.dto.BuildingDTO;

public class BuildingListerActivity extends AbstractListerActivity<BuildingDTO> {

    public BuildingListerActivity(Place place) {
        super(BuildingDTO.class, place, CrmSite.getViewFactory().getView(BuildingListerView.class));
    }

}
