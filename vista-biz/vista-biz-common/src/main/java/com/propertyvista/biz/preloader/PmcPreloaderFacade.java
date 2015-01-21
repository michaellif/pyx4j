/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 30, 2014
 * @author ernestog
 */
package com.propertyvista.biz.preloader;

import java.util.Map;

import com.pyx4j.entity.server.dataimport.DataPreloaderCollection;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.pmc.Pmc;

public interface PmcPreloaderFacade {

    public void clearPmc(String pmc, long start);

    public void resetPmcTables(String pmc, long start, boolean isExplicitTransaction);

    public void preloadPmc(String pmc, ResetType type, Map<String, String[]> params, OutputHolder out, long start, boolean isExplicitTransaction);

    public void preloadPmc(String pmc, ResetType type, long start, boolean isExplicitTransaction);

    public void preloadExistingPmc(Pmc pmc);

    public void resetAndPreloadPmcProcess(String pmc, ExecutionMonitor executionMonitor);

    public void resetAll(OutputHolder out, long start, DataPreloaderCollection preloaders);

    public void resetPmcCache(OutputHolder out);

    public void resetAllCache(OutputHolder out);

    public void dropForeignKeys(OutputHolder out, long start);

    public void dbIntegrityCheck(OutputHolder out, long start);

}
