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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import com.propertyvista.oapi.model.LeaseIO;
import com.propertyvista.oapi.model.TenantIO;
import com.propertyvista.oapi.model.TenantsIO;

public class RSLeaseServiceTest extends RSOapiTestBase {

    public RSLeaseServiceTest() throws Exception {
        super("com.propertyvista.oapi.rs");
    }

    @Test
    public void testUpdateTenants_() {
        WebResource webResource = resource();

        List<TenantIO> tenants = new ArrayList<TenantIO>();
        TenantIO tenant1 = new TenantIO("John", "Smith");
        TenantIO tenant2 = new TenantIO("James", "Smith");
        tenants.add(tenant1);
        tenants.add(tenant2);

        ClientResponse response = webResource.path("leases/testLeaseId/updateTenants_").accept(MediaType.APPLICATION_XML)
                .post(ClientResponse.class, new GenericEntity<List<TenantIO>>(tenants) {
                });
        Assert.assertEquals(ClientResponse.Status.OK, response.getClientResponseStatus());
    }

    @Test
    public void testUpdateTenants() {
        WebResource webResource = resource();

        TenantsIO tenantsIO = new TenantsIO();
        tenantsIO.tenants.add(new TenantIO("John", "Smith"));
        tenantsIO.tenants.add(new TenantIO("James", "Smith"));

        ClientResponse response = webResource.path("leases/testLeaseId/updateTenants").accept(MediaType.APPLICATION_XML).post(ClientResponse.class, tenantsIO);
        Assert.assertEquals(ClientResponse.Status.OK, response.getClientResponseStatus());
    }

    @Test
    public void testUpdateLease() {
        WebResource webResource = resource();

        LeaseIO lease = new LeaseIO("testId");
        ClientResponse response = webResource.path("leases/updateLease").accept(MediaType.APPLICATION_XML).post(ClientResponse.class, lease);
        Assert.assertEquals(ClientResponse.Status.OK, response.getClientResponseStatus());
    }
}
