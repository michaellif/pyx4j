/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-23
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import com.propertvista.generator.ReportGenerator;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;

import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata;

public class ReportPreloader extends AbstractDataPreloader {

    @Override
    public String create() {

// first demo report:        
        Persistence.service().persist(ReportGenerator.DefaultSystem1());
// the second one:
        Persistence.service().persist(ReportGenerator.DefaultSystem2());

        return "Created " + 2 + " demo reports";
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        return deleteAll(DashboardMetadata.class, GadgetMetadata.class);
    }

}
