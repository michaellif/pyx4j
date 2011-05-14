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
package com.propertyvista.portal.rpc.portal;

import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.annotations.PlaceProperties;

public class PortalSiteMap {
    public static String ARG_NAME_ITEM_ID = "ItemID";

    @PlaceProperties(caption = "Find an Apartment")
    public static class FindApartment extends AppPlace {

    }
/*
 * @NavigationItem(navigLabel = "CityMap")
 * 
 * @PlaceProperties(caption = "Find an Apartment")
 * public static class CityMap extends AppPlace {
 * }
 * 
 * @NavigationItem(navigLabel = "PropertyMap")
 * 
 * @PlaceProperties(caption = "Find an Apartment")
 * public static class PropertyMap extends AppPlace {
 * }
 * 
 * @NavigationItem(navigLabel = "Resident")
 * 
 * @PlaceProperties(caption = "Resident")
 * public static class Resident extends AppPlace {
 * 
 * @NavigationItem(navigLabel = "Resident")
 * 
 * @PlaceProperties(caption = "Resident Login")
 * public static class Login extends AppPlace {
 * }
 * }
 */

}
