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
package com.propertyvista.crm.client.ui.crud.complex;

import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;

/**
 * An enhanced version of {@Link DashboardView} that can be injected with customized {@link IBuildingGadget.FilterData} on the fly.
 * 
 */
public interface SlaveDashboardView extends DashboardView {
    public interface FilterDataGenerator {
        IBuildingGadget.FilterData getFilterData();
    }

    public interface SlavePresenter {

    }

    void applyFiltering();

    void setFilerDataGenerator(FilterDataGenerator generator);
}
