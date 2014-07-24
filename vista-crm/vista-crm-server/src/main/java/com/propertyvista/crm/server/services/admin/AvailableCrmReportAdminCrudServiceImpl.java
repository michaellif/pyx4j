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
package com.propertyvista.crm.server.services.admin;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;

import com.propertyvista.crm.rpc.services.admin.AvailableCrmReportAdminCrudService;
import com.propertyvista.domain.reports.AvailableCrmReport;

public class AvailableCrmReportAdminCrudServiceImpl extends AbstractCrudServiceImpl<AvailableCrmReport> implements AvailableCrmReportAdminCrudService {

    public AvailableCrmReportAdminCrudServiceImpl(Class<AvailableCrmReport> entityClass) {
        super(AvailableCrmReport.class);
    }

}
