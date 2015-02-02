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
package com.propertyvista.biz.preloader.pmc;

import java.util.Map;

import com.pyx4j.entity.server.dataimport.DataPreloaderCollection;

import com.propertyvista.biz.preloader.OutputHolder;
import com.propertyvista.biz.preloader.PmcPreloaderFacade;
import com.propertyvista.biz.preloader.ResetType;
import com.propertyvista.domain.pmc.Pmc;

public class PmcPreloaderFacadeImpl implements PmcPreloaderFacade {

    @Override
    public void resetPmcTables(String pmcDnsName) {
        PmcPreloaderManager.instance().resetPmcTables(pmcDnsName);
    }

    @Override
    public void resetAndPreloadPmcProcess(String pmcDnsName) {
        PmcPreloaderManager.instance().resetAndPreloadPmc(pmcDnsName);
    }

    @Override
    public void clearPmc(String pmcDnsName) {
        PmcPreloaderManager.instance().clearPmc(pmcDnsName);
    }

    @Override
    public void preloadPmc(String pmcDnsName, ResetType type, Map<String, String[]> params, OutputHolder o) {
        PmcPreloaderManager.instance().preloadPmc(pmcDnsName, type, params, o);
    }

    @Override
    public void preloadPmc(String pmcDnsName, ResetType type) {
        PmcPreloaderManager.instance().preloadPmc(pmcDnsName, type, null, null);
    }

    @Override
    public void preloadExistingPmc(Pmc pmc) {
        PmcPreloaderManager.instance().preloadExistingPmc(pmc);
    }

    @Override
    public void resetAll(OutputHolder o, DataPreloaderCollection operationPreloaders) {
        PmcPreloaderManager.instance().resetAll(o, operationPreloaders);
    }

    @Override
    public void resetPmcCache(OutputHolder out) {
        PmcPreloaderManager.instance().resetPmcCache(out);
    }

    @Override
    public void resetAllCache(OutputHolder out) {
        PmcPreloaderManager.instance().resetAllCache(out);
    }

    @Override
    public void dropForeignKeys(OutputHolder out) {
        PmcPreloaderManager.instance().dropForeignKeys(out);
    }

    @Override
    public void dbIntegrityCheck(OutputHolder out) {
        // TODO Not access to DBIntegrityCheckDeferredProcess at operations-server module
//        PmcPreloaderHelper.stopAndPrepareDBProcesses();
//        try {
//            ReportRequest reportdbo = new ReportRequest();
//            EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
//            criteria.add(PropertyCriterion.ne(criteria.proto().status(), PmcStatus.Created));
//            criteria.asc(criteria.proto().namespace());
//            reportdbo.setCriteria(criteria);
//            new DBIntegrityCheckDeferredProcess(reportdbo, false).execute();
//            PmcPreloaderHelper.recordOperation(ResetType.dbIntegrityCheck, start);
//        } catch (Throwable t) {
////            log.error("", t);
//            Persistence.service().rollback();
//            logError(out, t);
//        } finally {
//            PmcPreloaderHelper.raiseDBProcesses();
//            performResetFinallyActions();
//        }

    }

//    public static void logError(OutputHolder out, Throwable t) throws Error {
//        PmcPreloaderHelper.writeToOutput(out, "\nDB reset error:");
//        PmcPreloaderHelper.writeToOutput(out, t.getMessage());
//        if (null != out) {
//            throw new Error(t);
//        }
//    }

}
