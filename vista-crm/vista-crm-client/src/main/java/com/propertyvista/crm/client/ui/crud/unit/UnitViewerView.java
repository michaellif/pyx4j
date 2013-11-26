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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.site.client.ui.prime.form.IViewer;
import com.pyx4j.site.client.ui.prime.lister.ILister;

import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.MakeVacantConstraintsDTO;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.dto.AptUnitDTO;

public interface UnitViewerView extends IViewer<AptUnitDTO> {

    interface Presenter extends IViewer.Presenter {

        void scopeOffMarket(OffMarketType type);

        void scopeRenovation(LogicalDate renovationEndDate);

        void scopeAvailable();

        void makeVacant(LogicalDate vacantFrom);

        void createMaintenanceRequest();
    }

    ILister<AptUnitItem> getUnitItemsListerView();

    ILister<AptUnitOccupancySegment> getOccupanciesListerView();

    void setCanScopeOffMarket(boolean canScopeOffMarket);

    void setCanScopeAvailable(boolean canScopeAvailable);

    void setMinRenovationEndDate(LogicalDate minRenovationEndDate);

    void setMakeVacantConstraints(MakeVacantConstraintsDTO minMakeVacantStartDay);
}
