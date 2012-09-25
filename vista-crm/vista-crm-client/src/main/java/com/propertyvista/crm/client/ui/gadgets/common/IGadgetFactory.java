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
package com.propertyvista.crm.client.ui.gadgets.common;

import java.util.List;

import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public interface IGadgetFactory {

    IGadgetInstance createGadget(GadgetMetadata metadata) throws Error;

    Class<? extends GadgetMetadata> getGadgetMetadataClass();

    /** @return Return type of the gadget (is used for persistence system purposes and identification */
    String getType();

    String getName();

    List<String> getCategories();

    /** @return Return short explanation of gadget's abilities. */
    String getDescription();

    @Deprecated
    boolean isBuildingGadget();

    boolean isAcceptedBy(DashboardType dashboardType);

}