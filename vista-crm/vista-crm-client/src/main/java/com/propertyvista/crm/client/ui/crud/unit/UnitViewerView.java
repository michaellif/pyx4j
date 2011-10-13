/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-17
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.unit;

import com.pyx4j.site.client.ui.crud.IViewerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView;

import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy;
import com.propertyvista.dto.AptUnitDTO;

public interface UnitViewerView extends IViewerView<AptUnitDTO> {

    interface Presenter extends IViewerView.Presenter {

        IListerView.Presenter getUnitItemsPresenter();

        IListerView.Presenter getOccupanciesPresenter();
    }

    IListerView<AptUnitItem> getUnitItemsListerView();

    IListerView<AptUnitOccupancy> getOccupanciesListerView();
}
