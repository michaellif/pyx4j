/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 3, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Pair;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.essentials.server.dev.DataDump;

import com.propertyvista.portal.server.ptapp.PortalServicesTest;

public class TestUtil {
    private final static Logger log = LoggerFactory.getLogger(PortalServicesTest.class);

    public static Pair<String, String> createCaptcha() {
        return new Pair<String, String>("n/a", "x");
    }

    public static void assertEqual(String name, IEntity clientSide, IEntity received) {
        Path changePath = EntityGraph.getChangedDataPath(clientSide, received);
        if (changePath != null) {
            DataDump.dump("client", clientSide);
            DataDump.dump("server", received);
            log.debug("client {}", clientSide);
            log.debug("server {}", received);
            Assert.fail(name + " are not the same: " + changePath);
        }
    }
}
