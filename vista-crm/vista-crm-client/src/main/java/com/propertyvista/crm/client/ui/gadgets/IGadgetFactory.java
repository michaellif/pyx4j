/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 30, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets;

import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;

public interface IGadgetFactory {
    IGadgetInstanceBase createGadget(GadgetMetadata metadata) throws Error;

    /** @return Return type of the gadget (is used for persistence system purposes and identification */
    String getType();

    String getName();

    /** @return Return short explanation of gadget's abilities. */
    String getDescription();

    boolean isBuildingGadget();

    boolean isAcceptedBy(DashboardType dashboardType);

}