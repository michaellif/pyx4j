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
package com.propertyvista.oapi.v1.ws;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import com.propertyvista.oapi.v1.model.BuildingIO;
import com.propertyvista.oapi.v1.model.BuildingsIO;
import com.propertyvista.oapi.v1.model.UnitIO;

//http://localhost:8888/vista/interfaces/oapi/ws/WSPropertyService?wsdl
//https://static-22.birchwoodsoftwaregroup.com/interfaces/oapi/ws/WSPropertyService?wsdl

@WebService
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
public interface WSPropertyService {

    @WebMethod
    void createBuilding(@WebParam(name = "building") BuildingIO building);

    @WebResult(name = "buildings")
    BuildingsIO listAllBuildings();

    @WebResult(name = "building")
    BuildingIO getBuildingByPropertyCode(@WebParam(name = "propertyCode") String propertyCode);

    @WebResult(name = "units")
    List<UnitIO> listAllBuildingUnits(@WebParam(name = "propertyCode") String buildingCode);

    // update existing building

    // get building list (criteria?)

    //

    //================================

    // same for units (beds , bath, floorplan name), floorplans(beds , bath)?

//    createUnits(List<Unit>)
//
//
    void createUnit(@WebParam(name = "unitNumber") String unitNumber, @WebParam(name = "floorplanName") String floorplanName,
            @WebParam(name = "propertyCode") String propertyCode);
//
//    updateUnit(Unit)

}
