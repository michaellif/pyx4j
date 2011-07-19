/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.application;

import com.pyx4j.site.client.ui.crud.IListerView;

import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;

public interface ApplicationView {

    interface Presenter {

        IListerView.Presenter getBuildingPresenter();

        IListerView.Presenter getUnitPresenter();

        IListerView.Presenter getTenantPresenter();
    }

    IListerView<BuildingDTO> getBuildingListerView();

    IListerView<AptUnitDTO> getUnitListerView();

    IListerView<PotentialTenantInfo> getTenantListerView();
}
