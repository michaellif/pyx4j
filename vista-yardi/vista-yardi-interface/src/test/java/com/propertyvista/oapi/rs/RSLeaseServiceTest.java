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

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.oapi.model.LeaseIO;
import com.propertyvista.oapi.model.TenantIO;

public class RSLeaseServiceTest extends RSOapiTestBase {

    public RSLeaseServiceTest() throws Exception {
        super("com.propertyvista.oapi.rs");
    }

    @Before
    @Override
    public void initDB() throws Exception {
        super.initDB();

        TestLifecycle.testSession(null, VistaBasicBehavior.CRM);
        TestLifecycle.beginRequest();
    }

    @Before
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        TestLifecycle.tearDown();
    }

    // @Test
    public void testGetTenants_NonExistingLeaseId() {
        WebResource webResource = resource();
        GenericType<List<TenantIO>> gt = new GenericType<List<TenantIO>>() {
        };
        List<TenantIO> tenants = webResource.path("leases/mockId/tenants").get(gt);
        Assert.assertEquals(0, tenants.size());
    }

    // @Test
    public void testUpdateTenants() {
        WebResource webResource = resource();

        List<TenantIO> tenants = new ArrayList<TenantIO>();
        TenantIO tenant1 = new TenantIO("John", "Smith");
        TenantIO tenant2 = new TenantIO("James", "Smith");
        tenants.add(tenant1);
        tenants.add(tenant2);

        ClientResponse response = webResource.path("leases/testLeaseId/updateTenants").accept(MediaType.APPLICATION_XML)
                .post(ClientResponse.class, new GenericEntity<List<TenantIO>>(tenants) {
                });
        Assert.assertEquals(ClientResponse.Status.OK, response.getClientResponseStatus());
    }

    // @Test
    public void testUpdateLease() {
        WebResource webResource = resource();

        LeaseIO lease = new LeaseIO("testId");

        ClientResponse response = webResource.path("leases/updateLease").accept(MediaType.APPLICATION_XML).post(ClientResponse.class, lease);
        Assert.assertEquals(ClientResponse.Status.OK, response.getClientResponseStatus());
    }

    // @Test
    public void testGetLeases() {
        WebResource webResource = resource();
        GenericType<List<LeaseIO>> gt = new GenericType<List<LeaseIO>>() {
        };
        List<LeaseIO> leases = webResource.path("leases").get(gt);
        Assert.assertEquals(0, leases.size());
    }

    // @Test
    public void testGetLeases_NonExistingPropertyCode() {
        WebResource webResource = resource();
        GenericType<List<LeaseIO>> gt = new GenericType<List<LeaseIO>>() {
        };
        List<LeaseIO> leases = webResource.path("leases?propertyCode=MockCode").get(gt);
        Assert.assertEquals(0, leases.size());
    }

    @Test
    public void testGetLeaseById_NonExistingId() {
        WebResource webResource = resource();

        ClientResponse response = webResource.path("leases/mockId").get(ClientResponse.class);
        Assert.assertEquals(ClientResponse.Status.INTERNAL_SERVER_ERROR, response.getClientResponseStatus());
    }
}
