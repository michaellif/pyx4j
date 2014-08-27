/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 27, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.config.tests;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.rdb.IEntityPersistenceServiceRDB;
import com.pyx4j.entity.rdb.RDBUtils;
import com.pyx4j.entity.rdb.cfg.Configuration;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.server.contexts.NamespaceManager;
import com.pyx4j.server.mail.Mail;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.blob.LeaseTermAgreementDocumentBlob;
import com.propertyvista.domain.note.NotesAndAttachments;
import com.propertyvista.domain.policy.policies.YardiInterfacePolicy;
import com.propertyvista.domain.security.CustomerUserCredential;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.server.domain.IdAssignmentSequence;

public class VistaTestDBSetup {

    private static ServerSideConfiguration initOnce = null;

    public static synchronized void init() {
        if (initOnce == null) {
            DatabaseType databaseType = DatabaseType.HSQLDB;
            //databaseType = DatabaseType.MySQL;
            //databaseType = DatabaseType.Derby;
            //databaseType = DatabaseType.PostgreSQL;

            // Fail safe if somebody committed the file by mistake
            if (System.getProperty("bamboo.buildNumber") != null) {
                databaseType = DatabaseType.HSQLDB;
            }

            initOnce = new VistaTestsServerSideConfiguration(databaseType);
            ServerSideConfiguration.setInstance(initOnce);

            boolean forceSlowTests = (System.getProperty("test.db.slow") != null);
            //forceSlowTests = true;
            if (forceSlowTests) {
                VistaTestsConnectionPoolConfiguration.initSlowTests();
                prepareSlowPersistenceServices();
            }

            Mail.getMailService().setDisabled(true);
            initOperationsNamespace();
        }
        NamespaceManager.setNamespace(VistaTestsNamespaceResolver.demoNamespace);
    }

    /**
     * Resolves HSQL DB tables creation concurrency
     */
    public static void initOperationsNamespace() {
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {
            @Override
            public Void execute() {
                NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
                RDBUtils.ensureNamespace();
                RDBUtils.initNameSpaceSpecificEntityTables();
                return null;
            }
        });
    }

    /**
     * Resolves HSQL DB tables creation concurrency for HSQL Switch to TRANSACTION CONTROL MVCC
     *
     * All entity are not initialized because it takes allot of time in each test!
     *
     * TODO Find out why this tables initialization are causing problems
     */
    public static void initNamespace() {
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {
            @Override
            public Void execute() {
                RDBUtils.ensureNamespace();
                //RDBUtils.initAllEntityTables();
                List<Class<? extends IEntity>> classes = new ArrayList<Class<? extends IEntity>>();
                classes.add(IdAssignmentSequence.class);
                classes.add(Lease.class);
                classes.add(Lead.class);
                classes.add(LeaseTermAgreementDocumentBlob.class);
                classes.add(NotesAndAttachments.class);
                classes.add(YardiInterfacePolicy.class);
                classes.add(CustomerUserCredential.class);
                ((IEntityPersistenceServiceRDB) Persistence.service()).ensureSchemaModel(classes);
                return null;
            }
        });
    }

    public static void resetDatabase() {
        RDBUtils.resetDatabase();
        switch (((Configuration) initOnce.getPersistenceConfiguration()).databaseType()) {
        case PostgreSQL:
            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
            RDBUtils.ensureNamespace();
            NamespaceManager.setNamespace(VistaTestsNamespaceResolver.demoNamespace);
            RDBUtils.ensureNamespace();
            break;
        default:
            break;
        }
    }

    private static Set<String> slowPersistenceMethods = new HashSet<>();

    static {
        slowPersistenceMethods.add("startTransaction");
        slowPersistenceMethods.add("commit");
    }

    private static void prepareSlowPersistenceServices() {
        final IEntityPersistenceService origService = PersistenceServicesFactory.getPersistenceService();
        IEntityPersistenceService slowService;
        try {
            slowService = (IEntityPersistenceService) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[] { IEntityPersistenceServiceRDB.class }, new InvocationHandler() {

                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if (slowPersistenceMethods.contains(method.getName())) {
                                Thread.sleep(1000);
                            }
                            try {
                                return method.invoke(origService, args);
                            } catch (InvocationTargetException e) {
                                if (e.getCause() instanceof UndeclaredThrowableException) {
                                    throw (UndeclaredThrowableException) e.getCause();
                                } else {
                                    throw e.getCause();
                                }
                            }
                        }
                    });

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Can't create Slow EntityPersistenceService", e);
        }
        PersistenceServicesFactory.setPersistenceService(slowService);
    }
}
