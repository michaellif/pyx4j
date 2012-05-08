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

import java.util.Random;

import com.propertyvista.admin.domain.scheduler.RunDataStatus;
import com.propertyvista.admin.domain.scheduler.RunStats;

public class TestPmcProcess implements PmcProcess {

    @Override
    public RunDataStatus executePmc(RunStats stats) {

        Random random = new Random();
        int max = random.nextInt(100);

        stats.total().setValue((long) max);

        for (int i = 0; i < max; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            stats.processed().setValue((long) i);
        }

        return RunDataStatus.Processed;
    }

    @Override
    public boolean start() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void complete() {
        // TODO Auto-generated method stub

    }
}
