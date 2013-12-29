/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 2, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.settings.PmcCompanyInfo;

public class PmcInformationPreloader extends AbstractDataPreloader {

    @Override
    public String create() {
        PmcCompanyInfo pmcCompanyInfo = EntityFactory.create(PmcCompanyInfo.class);
        pmcCompanyInfo.companyName().setValue(VistaDeployment.getCurrentPmc().name().getStringView());
        Persistence.service().persist(pmcCompanyInfo);
        return null;
    }

    @Override
    public String delete() {
        return null;
    }

}
