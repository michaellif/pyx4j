/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;

import com.propertyvista.domain.financial.offeringnew.ServiceItemType;
import com.propertyvista.portal.server.generator.PmcGenerator;

public class PmcPreloader extends AbstractDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(PmcPreloader.class);

    public PmcPreloader() {

    }

    @Override
    public String create() {

        PmcGenerator generator = new PmcGenerator();

        List<ServiceItemType> types = generator.createServiceItemTypes();

        for (ServiceItemType type : types) {
            PersistenceServicesFactory.getPersistenceService().persist(type);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Created ").append(types.size()).append(" ServiceItemTypes");
        return sb.toString();
    }

    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(ServiceItemType.class);
        } else {
            return "This is production";
        }
    }

}
