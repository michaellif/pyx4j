/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 18, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.pmsite.server.model;

import org.junit.Test;

import com.pyx4j.entity.server.ServerEntityFactory;

import com.propertyvista.domain.property.asset.building.Building;

public class VistaDomainPojoTest {

    @Test
    public void testVistaDomainPojoCreation() {
        ServerEntityFactory.getPojoClass(Building.class);
    }

}
