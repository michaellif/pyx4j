/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 5, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.rs;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import com.propertyvista.oapi.model.BuildingsIO;
import com.propertyvista.oapi.model.UnitIO;

public class RSPropertyServiceTest extends RSOapiTestBase {

    public RSPropertyServiceTest() throws Exception {
        super("com.propertyvista.oapi.rs");
    }

    @Test
    public void testGetBuildings() {
        WebResource webResource = resource();
        BuildingsIO buildings = webResource.path("buildings").get(BuildingsIO.class);
    }

    @Test
    public void testGetAllUnitsByPropertyCode_NonExistingPropertyCode() {
        WebResource webResource = resource();
        GenericType<List<UnitIO>> gt = new GenericType<List<UnitIO>>() {
        };
        List<UnitIO> units = webResource.path("buildings/MockCode/units").get(gt);
        Assert.assertTrue(units.isEmpty());
    }
}
