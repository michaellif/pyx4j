/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;

import com.propertyvista.domain.communication.SystemEndpoint;
import com.propertyvista.domain.communication.SystemEndpoint.EndpointType;

public class SystemEnpointPreloader extends AbstractDataPreloader {

    @Override
    public String create() {
        createEnpoint(EndpointType.automate);
        createEnpoint(EndpointType.unassigned);
        return null;
    }

    private void createEnpoint(EndpointType epType) {
        SystemEndpoint ep = EntityFactory.create(SystemEndpoint.class);
        ep.type().setValue(epType);
        ep.name().setValue(epType.toString());
        PersistenceServicesFactory.getPersistenceService().persist(ep);
    }

    @Override
    public String delete() {
        return null;
    }
}
