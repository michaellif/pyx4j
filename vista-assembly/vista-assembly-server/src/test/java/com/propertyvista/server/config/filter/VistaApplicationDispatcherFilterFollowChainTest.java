/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 14, 2014
 * @author ernestog
 */
package com.propertyvista.server.config.filter;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Ignore;
import org.junit.Test;

import com.propertyvista.server.config.filter.base.VistaApplicationDispatcherFilterTestBase;

@Ignore
public class VistaApplicationDispatcherFilterFollowChainTest extends VistaApplicationDispatcherFilterTestBase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        log.info("VistaApplicationDispatcherFilterFollowChainTest initialized");
    }

    /**
     * Test wrong addresses. Should do chain in all cases.
     *
     * @throws IOException
     * @throws ServletException
     */

    @Test
    @Ignore
    // Why this failing ?
    public final void testFollowChain() throws IOException, ServletException {

        testChain("https://vista-22.birchwoodsoftwaregroup.com/index.html");

        testChain("https://vista-vista-22.birchwoodsoftwaregroup.com/index.html");

        testChain("https://port-vista-999.birchwoodsoftwaregroup.com/demo/");

        testChain("http://onboardingg.dev.birchwoodsoftwaregroup.com:8888/index.html");

        testChain("https://env-99.devpv.com/index.html");

    }
}
