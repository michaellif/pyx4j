/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 27, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.crm.server.openapi.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "buildings")
public class BuildingsRS {

    @XmlElement(name = "buildings")
    public List<BuildingRS> buildings = new ArrayList<BuildingRS>();

//    public static BuildingsRS valueOf(List<Building> buildingList) {
//        BuildingsRS buildings = new BuildingsRS();
//        buildings.buildings = new ArrayList<BuildingRS>();
//
//        for (Building building : buildingList) {
//            buildings.buildings.add(new BuildingRS(building));
//        }
//
//        return buildings;
//    }
}
