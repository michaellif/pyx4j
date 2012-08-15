/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 14, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard;

import java.util.Iterator;

import com.pyx4j.widgets.client.dashboard.IBoard;

import com.propertyvista.crm.client.ui.gadgets.common.IGadgetInstance;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public interface ILayoutManager {

    /** saves board layout to dashboard metadata */
    void saveLayout(DashboardMetadata dashboardMetadata, IBoard board);

    /** places gadgets in the board */
    void restoreLayout(DashboardMetadata dasboardMetadata, Iterator<IGadgetInstance> gadget, IBoard board);

}
