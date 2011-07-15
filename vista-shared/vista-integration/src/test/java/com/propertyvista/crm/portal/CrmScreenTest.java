/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-07-11
 * @author TPRGLET
 * @version $Id$
 */
package com.propertyvista.crm.portal;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.site.client.NavigationIDs;

import com.propertyvista.crm.CrmSeleniumTestBase;

public class CrmScreenTest extends CrmSeleniumTestBase {

    public void testCrmApplicationFlow() {
        login();
        selenium.click(new CompositeDebugId(NavigationIDs.Navigation_Folder, "Properties"));
        //selenium.click(new CompositeDebugId(NavigationIDs.Navigation_Item, "Buildings"));
        selenium.fireEvent(new CompositeDebugId(NavigationIDs.Navigation_Item, "Buildings").toString(), "click");
        //selenium.waitFor(new CompositeDebugId(NavigationIDs.Navigation_Button, NavigationIDs.ItemDescriptionIDs.Add_New_Item));
        //selenium.click(new CompositeDebugId(NavigationIDs.Navigation_Button, NavigationIDs.ItemDescriptionIDs.Add_New_Item).toString());
        selenium.fireEvent(new CompositeDebugId(NavigationIDs.Navigation_Button, NavigationIDs.ItemDescriptionIDs.Add_New_Item).toString(), "click");
//        selenium.waitFor(D.id(proto(BuildingDTO.class).info().name()).toString());
//        selenium.type(D.id(proto(BuildingDTO.class).info().name()), "Leon Tager");
//        selenium.setValue(D.id(proto(BuildingDTO.class).info().type()), BuildingInfo.Type.residential.name());
//        selenium.type(D.id(proto(BuildingDTO.class).info().address().streetNumber()), "57");
//        selenium.type(D.id(proto(BuildingDTO.class).info().address().streetName()), "King");
//        selenium.type(D.id(proto(BuildingDTO.class).info().address().streetType()), IAddressFull.StreetType.street.name());
//        selenium.type(D.id(proto(BuildingDTO.class).info().address().city()), "Toronto");
//        selenium.type(D.id(proto(BuildingDTO.class).info().address().province()), "Ontario");
//        selenium.type(D.id(proto(BuildingDTO.class).info().address().country()), "Canada");
//        selenium.type(D.id(proto(BuildingDTO.class).info().address().postalCode()), "M3C1A3");
    }
}
