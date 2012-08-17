/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 16, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.propertyvista.crm.client.ui.gadgets.common.IGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.common.IGadgetInstance;
import com.propertyvista.crm.client.ui.gadgets.other.BuildingListerGadget;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;

public class SystemGadgetDirectory implements IGadgetDirectory {

    private static List<IGadgetFactory> DIRECTORY = Arrays.asList(//@formatter:off            
            (IGadgetFactory) new BuildingListerGadget()
    );//@formatter:on

    @Override
    public IGadgetInstance createGadgetInstance(GadgetMetadata gadgetMetadata) {
        if (gadgetMetadata == null) {
            return null;
        }
        String requestedGadgetType = gadgetMetadata.cast().getObjectClass().getName();
        for (IGadgetFactory g : DIRECTORY) {
            if (g.getType().equals(requestedGadgetType)) {
                return g.createGadget(gadgetMetadata);
            }
        }
        return null;
    }

    @Override
    public Collection<? extends IGadgetFactory> getAvailableGadgets() {
        return DIRECTORY;
    }
}
