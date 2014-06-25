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

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

import com.propertyvista.oapi.model.LeaseIO;
import com.propertyvista.oapi.model.TenantIO;

public class RSLeaseServiceTest extends RSOapiTestBase {

    @Override
    protected Class<? extends Application> getServiceApplication() {
        return OpenApiRsApplication.class;
    }

    @Test
    public void testGetTenants_NonExistingLeaseId() {
        GenericType<List<TenantIO>> gt = new GenericType<List<TenantIO>>() {
        };
        List<TenantIO> tenants = target().path("leases/mockId/tenants").request().get(gt);
        Assert.assertEquals(0, tenants.size());
    }

    @Test
    public void testUpdateTenants() {
        List<TenantIO> tenants = new ArrayList<TenantIO>();
        TenantIO tenant1 = new TenantIO("John", "Smith");
        TenantIO tenant2 = new TenantIO("James", "Smith");
        tenants.add(tenant1);
        tenants.add(tenant2);

        Response response = target().path("leases/testLeaseId/updateTenants").request(MediaType.APPLICATION_XML)
                .post(Entity.xml(new GenericEntity<List<TenantIO>>(tenants) {
                }));
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    public void ignore_testUpdateLease() {
        LeaseIO lease = new LeaseIO("testId");

        Response response = target().path("leases/updateLease").request(MediaType.APPLICATION_XML).post(Entity.xml(lease));
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetLeases() {
        GenericType<List<LeaseIO>> gt = new GenericType<List<LeaseIO>>() {
        };
        List<LeaseIO> leases = target().path("leases").request().get(gt);
        Assert.assertEquals(0, leases.size());
    }

    @Test
    public void testGetLeases_NonExistingPropertyCode() {
        GenericType<List<LeaseIO>> gt = new GenericType<List<LeaseIO>>() {
        };
        try {
            List<LeaseIO> leases = target().path("leases?propertyCode=MockCode").request().get(gt);
        } catch (Exception ignore) {
            Assert.assertEquals(InternalServerErrorException.class, ignore.getClass());
        }
    }

    @Test
    public void testGetLeaseById_NonExistingId() {
        Response response = target().path("leases/mockId").request().get();
        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }
}
