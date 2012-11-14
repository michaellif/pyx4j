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

import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.propertyvista.oapi.model.BuildingsRS;

@WebService
@Path("/buildings")
public class PropertyService {

    public PropertyService() {
    }

    @GET
    public BuildingsRS listAllBuildings() {
        return new BuildingsRS();
    }

}
