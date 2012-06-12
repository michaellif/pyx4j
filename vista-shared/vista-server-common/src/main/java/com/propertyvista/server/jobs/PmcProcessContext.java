/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import java.util.Date;

import com.propertyvista.admin.domain.scheduler.RunStats;

public class PmcProcessContext {

    private final RunStats runStats;

    private final Date forDate;

    public PmcProcessContext(RunStats runStats, Date forDate) {
        this.runStats = runStats;
        this.forDate = forDate;
    }

    public RunStats getRunStats() {
        return runStats;
    }

    public Date getForDate() {
        return forDate;
    }

}
