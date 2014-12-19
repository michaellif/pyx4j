/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 19, 2014
 * @author ernestog
 */
package com.propertyvista.server.config.filter;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.unit.server.mock.MockHttpServletRequest;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.security.common.VistaApplication;

public class VistaNamespaceDataResolverTest extends VistaNamespaceResolverTestBase {

    protected final static Logger log = LoggerFactory.getLogger(VistaNamespaceDataResolverTest.class);

    @Test
    public final void testNamespaceDataResolver() {
        // Test 1
        req = new MockHttpServletRequest("http://vista-crm.local.devpv.com:8888/vista/crm/srv/IServiceAdapter/BuildingCrudService.list");
        testRetrievingData(req, VistaApplication.crm, "vista");

        // Test 2
        req = new MockHttpServletRequest("http://onboarding.dev.birchwoodsoftwaregroup.com:8888/");
        testRetrievingData(req, VistaApplication.onboarding, VistaNamespace.noNamespace);

        // Test 3
        req = new MockHttpServletRequest("http://operations.dev.devpv.com:80/operations/operations.nocache.js");
        testRetrievingData(req, VistaApplication.operations, VistaNamespace.operationsNamespace);

    }

    /**
     * Test three different scenarios accesing data:
     * - access only to VistaApplication (only VistaApplication is retrieved)
     * - access only to VistaNamespace (only VistaNamespace is retrieved)
     * - access to complete object (hole VistaNamespaceData object is retrieved)
     */
    private void testRetrievingData(HttpServletRequest req, VistaApplication app, String ns) {

        // VistaApplication data only
        Assert.assertTrue(getResolver(req).getVistaApplication() == app);

        // VistaNamespace data only
        Assert.assertTrue(getResolver(req).getVistaNamespace().equalsIgnoreCase(ns));

        // Hole VistaNamespaceData object
        VistaNamespaceData nsData = (VistaNamespaceData) getResolver(req).getNamespaceData();
        Assert.assertTrue(nsData.getApplication() == app);
        Assert.assertTrue(nsData.getNamespace().equalsIgnoreCase(ns));
    }

    private VistaNamespaceDataResolver getResolver(HttpServletRequest req) {
        return VistaNamespaceDataResolver.create(req);
    }

}
