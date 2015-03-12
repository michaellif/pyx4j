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
package com.propertyvista.biz.preloader.pmc;

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
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.server.dataimport.DataPreloaderCollection;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.quartz.SchedulerHelper;
import com.pyx4j.server.contexts.NamespaceManager;
import com.pyx4j.server.mail.Mail;

import com.propertyvista.biz.preloader.OutputHolder;
import com.propertyvista.biz.preloader.ResetType;
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

    public synchronized void clearPmc(final String pmcDnsName) {
        long operationStartTime = System.currentTimeMillis();
        stopCommunications();
        try {
            RDBUtils.deleteFromAllEntityTables();

            TaskRunner.runUnitOfWorkInOperationstNamespace(TransactionScopeOption.RequiresNew, new Executable<Void, RuntimeException>() {
                @Override
                public Void execute() {
                    NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
                    EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
                    criteria.add(PropertyCriterion.eq(criteria.proto().namespace(), pmcDnsName));
                    Persistence.service().delete(criteria);
                    return null;
                }
            });

            PmcPreloaderManager.recordOperation(ResetType.clearPmc, operationStartTime);

        } catch (Throwable t) {
            log.error("", t);
            throw new Error(t);
        } finally {
            startCommunications();
            performResetFinallyActions();
        }
    }

    public synchronized void resetPmcCache(OutputHolder output) {
        try {
            CacheService.reset();
            PmcPreloaderManager.writeToOutput(output, "\nCacheService.reset Ok");
        } catch (Throwable t) {
            logError(output, t);
            throw new Error(t);
        } finally {
            performResetFinallyActions();
        }
    }

    public synchronized void resetAllCache(OutputHolder output) {
        try {
            CacheService.resetAll();
            PmcPreloaderManager.writeToOutput(output, "\nCacheService.resetAll Ok");
        } catch (Throwable t) {
            logError(output, t);
            throw new Error(t);
        } finally {
            performResetFinallyActions();
        }
    }

    public synchronized void dropForeignKeys(OutputHolder output) {
        long operationStartTime = System.currentTimeMillis();
        stopCommunications();
        try {
            RDBUtils.dropAllForeignKeys();
            PmcPreloaderManager.recordOperation(ResetType.dropForeignKeys, operationStartTime);
        } catch (Throwable t) {
//            log.error("", t);
            Persistence.service().rollback();
            logError(output, t);
            throw new Error(t);
        } finally {
            startCommunications();
            performResetFinallyActions();
        }
    }

    public synchronized void resetPmcTables(final String pmcDnsName) {
        long operationStartTime = System.currentTimeMillis();
        stopCommunications();

        try {

            new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.BackgroundProcess).execute(new Executable<Void, RuntimeException>() {

                @Override
                public Void execute() {
                    CacheService.resetAll();
                    if (((IEntityPersistenceServiceRDB) Persistence.service()).getMultitenancyType() == MultitenancyType.SeparateSchemas) {
                        RDBUtils.resetSchema(pmcDnsName);
                    }
                    return null;
                }

            });

            PmcPreloaderManager.recordOperation(ResetType.resetPmc, operationStartTime);
        } catch (Throwable t) {
            log.error("", t);
            throw new Error(t);
        } finally {
            startCommunications();
        }

    }

    public synchronized void preloadPmc(final String pmcDnsName, final ResetType type, Map<String, String[]> params, OutputHolder output) {
        stopCommunications();
        final String requestNamespace = NamespaceManager.getNamespace();
        try {

            long operationStartTime = System.currentTimeMillis();

            log.debug("Preload PMC '{}'", pmcDnsName);

            final Pmc pmc = TaskRunner.runUnitOfWorkInOperationstNamespace(TransactionScopeOption.RequiresNew, new Executable<Pmc, RuntimeException>() {
                @Override
                public Pmc execute() {
                    deletePmcData(pmcDnsName);
                    Pmc pmc = PmcCreatorDev.createPmc(pmcDnsName, (type == ResetType.allMini));
                    return pmc;
                }
            });

            log.debug("Preload PMC... " + "PMC created");

            VistaDeployment.changePmcContext();

            NamespaceManager.setNamespace(pmc.namespace().getValue());

            writeToOutput(output, "\n--- Preload  " + pmcDnsName + " ---\n");

            TaskRunner.runUnitOfWorkInTargetNamespace(pmc.namespace().getValue(), TransactionScopeOption.RequiresNew, new Executable<Void, RuntimeException>() {
                @Override
                public Void execute() {
                    if (((EntityPersistenceServiceRDB) Persistence.service()).getMultitenancyType() == MultitenancyType.SeparateSchemas) {
                        RDBUtils.ensureNamespace();
                        RDBUtils.initAllEntityTables();
                    }
                    return null;
                }
            });

            writeToOutput(output, "\nPMC Tables created ", TimeUtils.secSince(operationStartTime));

            if (!EnumSet.of(ResetType.all, ResetType.allMini, ResetType.vistaMini, ResetType.vista, ResetType.vistaMax3000, ResetType.addPmcMockup,
                    ResetType.allAddMockup, ResetType.addPmcMockupTest1).contains(type)) {
                RDBUtils.deleteFromAllEntityTables();
            }
            CacheService.reset();

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
                cfg.numLandlords = 0;
                cfg.numResidentialBuildings = 0;
                cfg.numPotentialTenants2CreditCheck = 0;
                cfg.numPotentialTenants = 0;
                cfg.numTenants = 0;
                cfg.numLeads = 0;
                cfg.numUnAssigendTenants = 0;
            }

            // As this preload function is invoked from DBResetServlet and
            // nightly reset DEMO PMC process only, preload creditCheckPaymentPreloader also
            cfg.creditCheckPaymentPreloader = true;

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

                writeToOutput(output, "\n" + preloaders.exectutePreloadersCreate(dpisRun));
            } else {
                writeToOutput(output, "\n" + preloaders.preloadAll());
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

            log.info("Preloaded PMC '{}' {}", pmcDnsName, TimeUtils.secSince(operationStartTime));
            writeToOutput(output, "\nPreloaded PMC '" + pmcDnsName + "' " + TimeUtils.secSince(operationStartTime));
            ServerSideFactory.create(OperationsAlertFacade.class).record(pmc, "Preloaded PMC ''{0}'' {1}", pmcDnsName, TimeUtils.secSince(operationStartTime));

            PmcPreloaderManager.recordOperation(type, operationStartTime);
        } catch (Throwable t) {
            logError(output, t);
            throw new Error(t);
        } finally {
            NamespaceManager.setNamespace(requestNamespace);
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

            // Preload creditCheck Payments preloader also after Onboarding PMC creation
//            cfg.creditCheckPaymentPreloader = true;

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

    public synchronized void resetAll(OutputHolder output, DataPreloaderCollection operationPreloaders) {
        long operationStartTime = System.currentTimeMillis();
        stopCommunications();
        try {
            final String requestNamespace = NamespaceManager.getNamespace();
            Validate.isTrue(!VistaDeployment.isVistaProduction(), "Destruction is disabled");
            SchedulerHelper.shutdown();
            RDBUtils.resetDatabase();
            SchedulerHelper.dbReset();
            writeToOutput(output, "\nDB Dropped: " + TimeUtils.secSince(operationStartTime));
            Thread.sleep(150);
            SchedulerHelper.init();
            SchedulerHelper.setActive(true);
            log.debug("Initialize Admin");
            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
            try {
                RDBUtils.ensureNamespace();
                long resetStartTimeInMillis = System.currentTimeMillis();
                if (((EntityPersistenceServiceRDB) Persistence.service()).getMultitenancyType() == MultitenancyType.SeparateSchemas) {
                    RDBUtils.initNameSpaceSpecificEntityTables();
                    writeToOutput(output, "\nAdmin tables created: " + TimeUtils.secSince(resetStartTimeInMillis));
                } else {
                    RDBUtils.initAllEntityTables();
                    writeToOutput(output, "\nAll tables created: " + TimeUtils.secSince(resetStartTimeInMillis));
                }
                if (((EntityPersistenceServiceRDB) Persistence.service()).getDatabaseType() == DatabaseType.PostgreSQL) {
                    Persistence.service().commit();
                }

                CacheService.resetAll();

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
            Persistence.service().rollback();
            logError(output, t);
            throw new Error(t);
        } finally {
            startCommunications();
            performResetFinallyActions();
        }

    }

    public void resetAndPreloadPmc(final String pmcDnsName) {
        resetPmcTables(pmcDnsName);
        preloadPmc(pmcDnsName, ResetType.preloadPmcWithMockup, null, null);
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

    public static void recordOperation(ResetType type, long processStartTimeInMillis) {
        ServerSideFactory.create(OperationsAlertFacade.class).record(null, "DB operation {0} completed {1}", type.name(),
                TimeUtils.secSince(processStartTimeInMillis));
    }

    public void performResetFinallyActions() {
        DataGenerator.cleanup();
        CacheService.reset();
    }

    private static void logError(OutputHolder output, Throwable t) throws Error {
        PmcPreloaderManager.writeToOutput(output, "\nDB error:");
        PmcPreloaderManager.writeToOutput(output, t.getMessage() == null ? t.toString() : t.getMessage());
        if (null == output) {
            log.error("", t);
        }
        throw new Error(t);
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
