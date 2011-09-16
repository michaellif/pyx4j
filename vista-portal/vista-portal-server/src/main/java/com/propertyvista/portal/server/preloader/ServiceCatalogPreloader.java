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

import com.propertvista.generator.ServiceTypesGenerator;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;

import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItemType;

public class ServiceCatalogPreloader extends AbstractDataPreloader {

    public ServiceCatalogPreloader() {

    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(ServiceItemType.class, Service.class, Feature.class, Concession.class);
        } else {
            return "This is production";
        }
    }

    @Override
    public String create() {

        ServiceTypesGenerator generator = new ServiceTypesGenerator();
        Persistence.service().persist(generator.getServiceItemTypes());
        Persistence.service().persist(generator.getFeatureItemTypes());

        StringBuilder sb = new StringBuilder();
        sb.append("Created ").append(generator.getServiceItemTypes().size() + generator.getFeatureItemTypes().size()).append(" ChargeItemType");
        return sb.toString();
    }

}
