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
 */
package com.propertyvista.crm.client.ui.crud.unit;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeViewerView;

import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.MakeVacantConstraintsDTO;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.dto.AptUnitDTO;

public interface UnitViewerView extends IPrimeViewerView<AptUnitDTO> {

    interface Presenter extends IPrimeViewerView.IPrimeViewerPresenter {

        void scopeOffMarket(OffMarketType type);

        void scopeRenovation(LogicalDate renovationEndDate);

        void scopeAvailable();

        void makeVacant(LogicalDate vacantFrom);

        void createMaintenanceRequest();

        void updateAvailabilityFromYardi();
    }

    UnitItemLister getUnitItemsListerView();

    UnitOccupancyLister getOccupanciesListerView();

    void setCanScopeOffMarket(boolean canScopeOffMarket);

    void setCanScopeAvailable(boolean canScopeAvailable);

    void setMinRenovationEndDate(LogicalDate minRenovationEndDate);

    void setMakeVacantConstraints(MakeVacantConstraintsDTO minMakeVacantStartDay);
}
