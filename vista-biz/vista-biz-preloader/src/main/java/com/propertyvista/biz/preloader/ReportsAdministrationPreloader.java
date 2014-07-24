/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 24, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.preloader;

import java.util.EnumSet;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;

import com.propertyvista.domain.reports.AvailableCrmReport;
import com.propertyvista.domain.reports.AvailableCrmReport.CrmReportType;

public class ReportsAdministrationPreloader extends AbstractDataPreloader {

    @Override
    public String create() {
        for (CrmReportType crmReportType : EnumSet.allOf(CrmReportType.class)) {
            AvailableCrmReport r = EntityFactory.create(AvailableCrmReport.class);
            r.reportType().setValue(crmReportType);
            r.roles().add(CrmRolesPreloader.getDefaultRole());
            Persistence.service().persist(r);
        }
        return null;
    }

    @Override
    public String delete() {
        return null;
    }

}
