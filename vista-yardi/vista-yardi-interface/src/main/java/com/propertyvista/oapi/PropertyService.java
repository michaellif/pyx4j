/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 13, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import com.propertyvista.oapi.model.BuildingRS;

@WebService
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
public interface PropertyService {

    @WebMethod
    void createBuilding(@WebParam(name = "building") BuildingRS building);

    @WebResult(name = "building")
    List<BuildingRS> getAllBuildings();

    @WebResult(name = "building")
    BuildingRS getBuildingByPropertyCode(@WebParam(name = "propertyCode") String propertyCode);

    // update existing building

    // get building by id

    // get building list (criteria?)

    //

    //================================

    // same for units (beds , bath, floorplan name), floorplans(beds , bath)?

//    createUnits(List<Unit>)
//    
//    createUnit(Unit)
//    
//    updateUnit(Unit)

}
