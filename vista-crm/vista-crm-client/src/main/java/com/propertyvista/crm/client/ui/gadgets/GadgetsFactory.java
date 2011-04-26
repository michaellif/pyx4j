/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets;

import com.propertyvista.crm.rpc.domain.GadgetMetadata;
import com.propertyvista.crm.rpc.domain.GadgetMetadata.GadgetType;

import com.pyx4j.dashboard.client.IGadget;

public class GadgetsFactory {

    public static IGadget createGadget(GadgetType type, GadgetMetadata metaData) {
        switch (type) {
        case Demo:
            return new DemoGadget(metaData);
        case BuildingLister:
            return new BuildingListerGadget(metaData);
        }
        return null;
    }
}
