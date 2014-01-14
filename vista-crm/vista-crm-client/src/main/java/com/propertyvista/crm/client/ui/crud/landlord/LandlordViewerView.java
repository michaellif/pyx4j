/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.landlord;

import java.util.List;

import com.pyx4j.site.client.ui.prime.form.IViewer;

import com.propertyvista.crm.client.visor.dashboard.IDashboardVisorController;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.LandlordDTO;

public interface LandlordViewerView extends IViewer<LandlordDTO> {

    interface Presenter extends IViewer.Presenter {

        IDashboardVisorController getDashboardController(DashboardMetadata dashboardMetadata, List<Building> buildings);
    }

}
