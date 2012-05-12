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

public interface PmcProcess {

    /**
     * This is executed in shared context to initialize the process.
     * 
     * @return false if this job needs to sleep and rerun again when data is ready.
     */
    boolean start();

    /**
     * This is executed in each selected PMC context to to the work.
     * Any modifications to DB should be committed.
     * 
     * Use PmcProcessContext.setRunStats(...) to update PMC statistics
     */
    void executePmcJob();

    /**
     * This is executed in shared context to finalize the process.
     */
    void complete();

}
