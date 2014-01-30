/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 14, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.prospect;

import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.annotations.PlaceProperties;
import com.pyx4j.site.shared.meta.PublicPlace;

import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class ProspectPortalSiteMap extends PortalSiteMap {

    public static String ARG_ILS_BUILDING_ID = "building";

    public static String ARG_ILS_FLOORPLAN_ID = "floorplan";

    public static String ARG_ILS_UNIT_ID = "unit";

    @PlaceProperties(caption = "Prospect Registration")
    public static class Registration extends AppPlace implements PublicPlace {
    }

    @PlaceProperties(navigLabel = "Select Application", caption = "Select Application")
    public static class ApplicationContextSelection extends AppPlace {
    }

    public static class Status extends AppPlace {
    }

    public static class Application extends AppPlace {
    }

    public static class ApplicationConfirmation extends AppPlace {
    }
}
