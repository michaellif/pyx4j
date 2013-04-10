/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-09
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.reference.geo.googleapis;

import org.junit.Assert;
import org.junit.Test;

public class GoogleMapRestServiceTest {

    @Test
    public void testGeocode() {

        GeocodeResponse result = GoogleMapRestService.getGeocode("1600 Amphitheatre Parkway, Mountain View, CA");

        Assert.assertEquals("37.4214009", result.result.geometry.location.lat);
        Assert.assertEquals("-122.0853701", result.result.geometry.location.lng);

    }

}
