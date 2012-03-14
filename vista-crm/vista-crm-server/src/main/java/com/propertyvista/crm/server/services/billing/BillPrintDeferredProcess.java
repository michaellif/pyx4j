/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author Artyom
 * @version $Id$
 */
package com.propertyvista.crm.server.services.billing;

import com.pyx4j.entity.report.JasperFileFormat;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.server.deferred.IDeferredProcess;

import com.propertyvista.domain.financial.billing.Bill;

public class BillPrintDeferredProcess implements IDeferredProcess {

    public BillPrintDeferredProcess(EntityQueryCriteria<Bill> entityQueryCriteria, JasperFileFormat pdf) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void execute() {
        // TODO Auto-generated method stub

    }

    @Override
    public void cancel() {
        // TODO Auto-generated method stub

    }

    @Override
    public DeferredProcessProgressResponse status() {
        // TODO Auto-generated method stub
        return null;
    }

}
