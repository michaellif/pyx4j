/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.domain.scheduler.Trigger;

public interface OperationsTriggerFacade {

    public void startProcess(PmcProcessType processType);

    public Run startProcess(PmcProcessType processType, Pmc pmcId, LogicalDate executionDate);

    public Run startProcess(Trigger triggerId, Pmc pmcId, LogicalDate executionDate);

}
