/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 9, 2015
 * @author ernestog
 */
package com.propertyvista.biz.preloader.pmc.helper;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Callable;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.rdb.IEntityPersistenceServiceRDB;
import com.pyx4j.entity.rdb.RDBUtils;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.rdb.cfg.Configuration.MultitenancyType;
import com.pyx4j.entity.rpc.DataPreloaderInfo;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.DataPreloaderCollection;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.quartz.SchedulerHelper;
import com.pyx4j.server.contexts.NamespaceManager;
import com.pyx4j.server.mail.Mail;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.preloader.OutputHolder;
import com.propertyvista.biz.preloader.ResetType;
import com.propertyvista.biz.preloader.pmc.CommunicationsHandler;
import com.propertyvista.biz.preloader.pmc.PmcCreatorDev;
import com.propertyvista.biz.system.OperationsAlertFacade;
import com.propertyvista.biz.system.OperationsTriggerFacade;
import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.biz.system.VistaSystemFacade;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.misc.VistaDataPreloaderParameter;
import com.propertyvista.misc.VistaDevPreloadConfig;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.server.TaskRunner;

public class PmcPreloaderManager implements CommunicationsHandler {

    private static final Logger log = LoggerFactory.getLogger(PmcPreloaderManager.class);

    private PmcPreloaderManager() {

    }

    private static class SingletonHolder {
        public static final PmcPreloaderManager INSTANCE = new PmcPreloaderManager();
    }

    public static PmcPreloaderManager instance() {
        return SingletonHolder.INSTANCE;
    }

    public synchronized void clearPmc(String pmcDnsName, long start) {
        stopCommunications();
        try {
            RDBUtils.deleteFromAllEntityTables();
            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
            EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().namespace(), pmcDnsName));
            Persistence.service().delete(criteria);
            Persistence.service().commit();

            PmcPreloaderManager.recordOperation(ResetType.clearPmc, start);

        } catch (Throwable t) {
            log.error("", t);
            Persistence.service().rollback();
        } finally {
            startCommunications();
            performResetFinallyActions();
        }
    }

    public synchronized void resetPmcCache(OutputHolder out) {
        try {
            CacheService.reset();
            PmcPreloaderManager.writeToOutput(out, "\nCacheService.reset Ok");
        } catch (Throwable t) {
            logError(out, t);
        } finally {
            performResetFinallyActions();
        }
    }

    public synchronized void resetAllCache(OutputHolder out) {
        try {
            CacheService.resetAll();
            PmcPreloaderManager.writeToOutput(out, "\nCacheService.resetAll Ok");
        } catch (Throwable t) {
            logError(out, t);
        } finally {
            performResetFinallyActions();
        }
    }

    public synchronized void dropForeignKeys(OutputHolder out, long start) {
        stopCommunications();
        try {
            RDBUtils.dropAllForeignKeys();
            PmcPreloaderManager.recordOperation(ResetType.dropForeignKeys, start);
        } catch (Throwable t) {
//            log.error("", t);
            Persistence.service().rollback();
            logError(out, t);
        } finally {
            startCommunications();
            performResetFinallyActions();
        }
    }

    public synchronized void resetPmcTables(String pmcDnsName, long start, boolean isExplicitTransaction) {
        stopCommunications();

        try {
            final String requestNamespace = NamespaceManager.getNamespace();
            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
            try {
                CacheService.resetAll();
                if (((IEntityPersistenceServiceRDB) Persistence.service()).getMultitenancyType() == MultitenancyType.SeparateSchemas) {
                    RDBUtils.resetSchema(pmcDnsName);
                }
                if (isExplicitTransaction) {
                    Persistence.service().commit();
                }

            } finally {
                NamespaceManager.setNamespace(requestNamespace);
            }
            PmcPreloaderManager.recordOperation(ResetType.resetPmc, start);
        } catch (Throwable t) {
            log.error("", t);
            Persistence.service().rollback();
            throw new Error(t);
        } finally {
            startCommunications();
//            performResetFinallyActions();
        }

    }

    public synchronized void preloadPmc(String pmcDnsName, ResetType type, Map<String, String[]> params, OutputHolder out, long start,
            boolean isExplicitTransacion) {
        stopCommunications();
        try {
            long pmcStart = System.currentTimeMillis();
            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
            log.debug("Preload PMC '{}'", pmcDnsName);

            deletePmcData(pmcDnsName);

            final Pmc pmc = PmcCreatorDev.createPmc(pmcDnsName, (type == ResetType.allMini));

//            if PersistenceServicesFactory.getPersistenceService().isExplicitTransaction()
            if (isExplicitTransacion) {
                Persistence.service().commit();
            }

            VistaDeployment.changePmcContext();

            NamespaceManager.setNamespace(pmc.namespace().getValue());

            writeToOutput(out, "\n--- Preload  " + pmcDnsName + " ---\n");
            if (((EntityPersistenceServiceRDB) Persistence.service()).getMultitenancyType() == MultitenancyType.SeparateSchemas) {
                RDBUtils.ensureNamespace();
                RDBUtils.initAllEntityTables();
                if (((EntityPersistenceServiceRDB) Persistence.service()).getDatabaseType() == DatabaseType.PostgreSQL) {
                    if (isExplicitTransacion) {
                        Persistence.service().commit();
                    }
                }
                writeToOutput(out, "PMC Tables created ", TimeUtils.secSince(pmcStart));
            }

            if (!EnumSet.of(ResetType.all, ResetType.allMini, ResetType.vistaMini, ResetType.vista, ResetType.vistaMax3000, ResetType.addPmcMockup,
                    ResetType.allAddMockup, ResetType.addPmcMockupTest1).contains(type)) {
                RDBUtils.deleteFromAllEntityTables();
            }
            CacheService.reset();

//        DataPreloaderCollection preloaders = ((VistaServerSideConfiguration) ServerSideConfiguration.instance()).getDataPreloaders();
            DataPreloaderCollection preloaders = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getDataPreloaders();

            VistaDevPreloadConfig cfg;
            switch (type) {
            case preloadPmcWithMockup:
            case addPmcMockup:
            case addPmcMockupTest1:
            case allAddMockup:
            case allWithMockup:
                cfg = VistaDevPreloadConfig.createMockup();
                break;
            case vistaMini:
            case allMini:
                cfg = VistaDevPreloadConfig.createUIDesignMini();
                break;
            case vistaMax3000:
                cfg = VistaDevPreloadConfig.createPerfomanceMax(3000);
                break;
            default:
                cfg = VistaDevPreloadConfig.createDefault();
            }
            //TODO fix LeasePreloader
            if (pmcDnsName.equals(DemoPmc.star.name()) && (type != ResetType.allMini)) {
                cfg.numComplexes = 0;
                cfg.numResidentialBuildings = 0;
                cfg.numPotentialTenants2CreditCheck = 0;
                cfg.numPotentialTenants = 0;
                cfg.numTenants = 0;
                cfg.numLeads = 0;
                cfg.numUnAssigendTenants = 0;
            }
            setPreloadConfigParameter(params, cfg);
            preloaders.setParameterValue(VistaDataPreloaderParameter.devPreloadConfig.name(), cfg);
            preloaders.setParameterValue(VistaDataPreloaderParameter.pmcName.name(), pmc.name().getStringView());

            if (type.name().toLowerCase().contains("add")) {
                Vector<DataPreloaderInfo> dpis = preloaders.getDataPreloaderInfo();
                Vector<DataPreloaderInfo> dpisRun = new Vector<DataPreloaderInfo>();
                String mockupClassNamefragment = "Mockup";
                for (DataPreloaderInfo info : dpis) {
                    info.setParameters((HashMap<String, Serializable>) preloaders.getParametersValues());
                    switch (type) {
                    case addPmcMockupTest1:
                        mockupClassNamefragment = "MockupTenantPreloader";
                    case allAddMockup:
                    case addPmcMockup:
                        if (info.getDataPreloaderClassName().contains(mockupClassNamefragment)) {
                            dpisRun.add(info);
                            break;
                        }
                    default:
                        break;
                    }
                }

                writeToOutput(out, preloaders.exectutePreloadersCreate(dpisRun));
            } else {
                writeToOutput(out, preloaders.preloadAll());
            }

            if (pmcDnsName.equals(DemoPmc.star.name()) && (type == ResetType.all)) {
                TaskRunner.runInOperationsNamespace(new Callable<Void>() {
                    @Override
                    public Void call() {
                        ServerSideFactory.create(OperationsTriggerFacade.class).startProcess(PmcProcessType.yardiImportProcess, pmc, null);
                        return null;
                    }
                });
            }

            CacheService.reset();

            log.info("Preloaded PMC '{}' {}", pmcDnsName, TimeUtils.secSince(pmcStart));
            writeToOutput(out, "Preloaded PMC '" + pmcDnsName + "' " + TimeUtils.secSince(pmcStart));
            ServerSideFactory.create(OperationsAlertFacade.class).record(pmc, "Preloaded PMC ''{0}'' {1}", pmcDnsName, TimeUtils.secSince(pmcStart));

            PmcPreloaderManager.recordOperation(type, start);
        } catch (Throwable t) {
            log.error("", t);
            Persistence.service().rollback();
            throw new Error(t);
        } finally {
            startCommunications();
            performResetFinallyActions();
        }
    }

    public synchronized void preloadExistingPmc(Pmc pmc) {
        stopCommunications();
        try {
            NamespaceManager.setNamespace(pmc.namespace().getValue());

            CacheService.reset();

            DataPreloaderCollection preloaders = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getDataPreloaders();

            VistaDevPreloadConfig cfg = VistaDevPreloadConfig.createDefault();
            preloaders.setParameterValue(VistaDataPreloaderParameter.devPreloadConfig.name(), cfg);
            preloaders.setParameterValue(VistaDataPreloaderParameter.pmcName.name(), pmc.name().getStringView());

            log.info(preloaders.preloadAll());

            CacheService.reset();

        } catch (Throwable t) {
            log.error("", t);
            Persistence.service().rollback();
            throw new Error(t);
        } finally {
            startCommunications();
            performResetFinallyActions();
        }
    }

    public synchronized void resetAll(OutputHolder out, long start, DataPreloaderCollection operationPreloaders) {
        stopCommunications();
        try {
            final String requestNamespace = NamespaceManager.getNamespace();
            Validate.isTrue(!VistaDeployment.isVistaProduction(), "Destruction is disabled");
            SchedulerHelper.shutdown();
            RDBUtils.resetDatabase();
            SchedulerHelper.dbReset();
            writeToOutput(out, "DB Dropped: " + TimeUtils.secSince(start));
            Thread.sleep(150);
            SchedulerHelper.init();
            SchedulerHelper.setActive(true);
            log.debug("Initialize Admin");
            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
            try {
                RDBUtils.ensureNamespace();
                long astart = System.currentTimeMillis();
                if (((EntityPersistenceServiceRDB) Persistence.service()).getMultitenancyType() == MultitenancyType.SeparateSchemas) {
                    RDBUtils.initNameSpaceSpecificEntityTables();
                    writeToOutput(out, "Admin tables created: " + TimeUtils.secSince(astart));
                } else {
                    RDBUtils.initAllEntityTables();
                    writeToOutput(out, "All tables created: " + TimeUtils.secSince(astart));
                }
                if (((EntityPersistenceServiceRDB) Persistence.service()).getDatabaseType() == DatabaseType.PostgreSQL) {
                    Persistence.service().commit();
                }

                CacheService.resetAll();

//                new VistaOperationsDataPreloaders().preloadAll();
                operationPreloaders.preloadAll();
                Persistence.service().commit();
            } finally {
                NamespaceManager.setNamespace(requestNamespace);
            }

            if (((EntityPersistenceServiceRDB) Persistence.service()).getMultitenancyType() == MultitenancyType.SeparateSchemas) {
                NamespaceManager.setNamespace(VistaNamespace.expiringNamespace);
                try {
                    RDBUtils.ensureNamespace();
                    RDBUtils.initNameSpaceSpecificEntityTables();
                    Persistence.service().commit();
                } finally {
                    NamespaceManager.setNamespace(requestNamespace);
                }
            }
        } catch (Throwable t) {
            log.error("", t);
            Persistence.service().rollback();
            throw new Error(t);
        } finally {
            startCommunications();
            performResetFinallyActions();
        }

    }

    public void resetAndPreloadPmc(final String pmcDnsName, final ExecutionMonitor executionMonitor) {
        try {
            resetPmcTables(pmcDnsName, System.currentTimeMillis(), false);
//            executionMonitor.addProcessedEvent("Reset PMCs", "Pmc {0} reseted", pmcDnsName);
            preloadPmc(pmcDnsName, ResetType.preloadPmc, null, null, System.currentTimeMillis(), false);
//            executionMonitor.addProcessedEvent("Preloaded PMCs", "Pmc {0} reseted", pmcDnsName);
        } catch (Throwable error) {
            executionMonitor.addErredEvent("Error reseting and preload PMC. ", error);
        }
    }

    public static void writeToOutput(OutputHolder output, String... messages) {
        if (output != null) {
            try {
                output.o(messages);
            } catch (IOException e) {
                log.error("Error writing to outputHolder", e);
            }
        } else {
            log.info(messages[0]);
        }
    }

    private static void setPreloadConfigParameter(Map<String, String[]> params, VistaDevPreloadConfig cfg) {
        if (params == null) {
            return;
        }

        for (Field field : cfg.getClass().getFields()) {
            String[] values = params.get(field.getName());
            if (values != null) {
                String value = values[0];
                if (value != null) {
                    try {
                        if (field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
                            field.setInt(cfg, Integer.valueOf(value));
                        } else if (field.getType().equals(Long.class) || field.getType().equals(long.class)) {
                            field.setLong(cfg, Long.valueOf(value));
                        } else if (field.getType().equals(String.class)) {
                            field.set(cfg, value);
                        }
                    } catch (Throwable e) {
                        throw new Error(e);
                    }
                }
            }
        }
    }

    private static void deletePmcData(String pmcDnsName) {
        EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().dnsName(), pmcDnsName));
        ServerSideFactory.create(PmcFacade.class).deleteAllPmcData(Persistence.service().retrieve(criteria));
    }

    public static void recordOperation(ResetType type, long start) {
        ServerSideFactory.create(OperationsAlertFacade.class).record(null, "DB operation {0} completed {1}", type.name(), TimeUtils.secSince(start));
    }

    public void performResetFinallyActions() {
        DataGenerator.cleanup();
        CacheService.reset();
    }

    private static void logError(OutputHolder out, Throwable t) throws Error {
        PmcPreloaderManager.writeToOutput(out, "\nDB reset error:");
        PmcPreloaderManager.writeToOutput(out, t.getMessage());
        if (null != out) {
            throw new Error(t);
        }
    }

    @Override
    public synchronized void startCommunications() {
        ServerSideFactory.create(VistaSystemFacade.class).setCommunicationsDisabled(false);
        Mail.getMailService().setDisabled(false);
    }

    @Override
    public synchronized void stopCommunications() {
        Mail.getMailService().setDisabled(true);
        ServerSideFactory.create(VistaSystemFacade.class).setCommunicationsDisabled(true);
    }

}
