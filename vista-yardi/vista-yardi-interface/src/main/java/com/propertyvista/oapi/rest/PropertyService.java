/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 26, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.propertyvista.oapi.PropertyFacade;
import com.propertyvista.oapi.model.BuildingsRS;

//http://localhost:8888/vista/interfaces/oapi/rs/propertyService/listAllBuildings
//https://static-22.birchwoodsoftwaregroup.com/interfaces/oapi/rs/propertyService/listAllBuildings
@Path("/propertyService")
public class PropertyService {

    public PropertyService() {
    }

    @GET
    @Path("listAllBuildings")
    @Produces({ MediaType.APPLICATION_XML })
    public BuildingsRS listAllBuildings() {
        return PropertyFacade.listAllBuildings();
    }

}
