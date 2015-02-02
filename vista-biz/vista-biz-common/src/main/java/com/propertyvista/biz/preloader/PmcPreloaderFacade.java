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

import com.propertyvista.domain.pmc.Pmc;

public interface PmcPreloaderFacade {

    public void clearPmc(String pmc);

    public void resetPmcTables(String pmc);

    public void preloadPmc(String pmc, ResetType type, Map<String, String[]> params, OutputHolder out);

    public void preloadPmc(String pmc, ResetType type);

    public void preloadExistingPmc(Pmc pmc);

    public void resetAndPreloadPmc(String pmc);

    public void resetAll(OutputHolder out, DataPreloaderCollection preloaders);

    public void resetPmcCache(OutputHolder out);

    public void resetAllCache(OutputHolder out);

    public void dropForeignKeys(OutputHolder out);

    public void dbIntegrityCheck(OutputHolder out);

}
