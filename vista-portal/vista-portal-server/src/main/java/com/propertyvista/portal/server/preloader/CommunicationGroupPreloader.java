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

import com.propertyvista.domain.communication.CommunicationGroup;
import com.propertyvista.domain.communication.CommunicationGroup.EndpointGroup;
import com.propertyvista.domain.security.CrmRole;

public class CommunicationGroupPreloader extends AbstractDataPreloader {

    @Override
    public String create() {

        createEnpoint(EndpointGroup.Commandant, CrmRolesPreloader.getDefaultRole(), CrmRolesPreloader.getCommandantRole());
        return null;
    }

    private void createEnpoint(EndpointGroup epType, CrmRole... defaultRole) {
        CommunicationGroup ep = EntityFactory.create(CommunicationGroup.class);
        ep.type().setValue(epType);
        ep.name().setValue(epType.toString());
        ep.isPredefined().setValue(true);
        if (defaultRole != null && defaultRole.length > 0) {
            for (int i = 0; i < defaultRole.length; ++i)
                ep.roles().add(defaultRole[i]);
        }
        PersistenceServicesFactory.getPersistenceService().persist(ep);
    }

    @Override
    public String delete() {
        return null;
    }
}
