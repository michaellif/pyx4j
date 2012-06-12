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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPmcProcess implements PmcProcess {

    private static final Logger log = LoggerFactory.getLogger(TestPmcProcess.class);

    @Override
    public boolean start(PmcProcessContext context) {
        log.info("Test Job started");
        return true;
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {

        Random random = new Random();
        int max = random.nextInt(300);

        context.getRunStats().total().setValue((long) max);
        context.getRunStats().failed().setValue(0L);

        for (int i = 0; i < max; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            context.getRunStats().processed().setValue((long) i);
        }

    }

    @Override
    public void complete(PmcProcessContext context) {
        log.info("Test Job complete");
    }
}
