/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Feb 14, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.server.mail;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.AbstractOutgoingMailQueue;
import com.pyx4j.entity.shared.AbstractOutgoingMailQueue.MailQueueStatus;
import com.pyx4j.server.contexts.NamespaceManager;

public class MailQueue implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(MailQueue.class);

    private static Map<String, Class<? extends AbstractOutgoingMailQueue>> persistableEntities = new HashMap<>();

    private static Map<String, IMailServiceConfigConfiguration> configurations = new HashMap<>();

    private static MailQueue instance;

    private boolean shutdown = false;

    private final Object monitor = new Object();

    private MailQueue() {
    }

    public static void initialize(IMailServiceConfigConfiguration config, Class<? extends AbstractOutgoingMailQueue> persistableEntityClass) {
        persistableEntities.put(config.configurationId(), persistableEntityClass);
        configurations.put(config.configurationId(), config);
        init();
    }

    private static synchronized void init() {
        if (instance == null) {
            instance = new MailQueue();
            Thread caseWatch = new Thread(instance, "MailQueueDeliveryThread");
            caseWatch.setDaemon(true);
            caseWatch.start();
        }
    }

    public static void shutdown() {
        if (instance != null) {
            instance.shutdown = true;
            synchronized (instance.monitor) {
                instance.monitor.notifyAll();
            }
            instance = null;
        }
    }

    public static void sendQueued() {
        synchronized (instance.monitor) {
            instance.monitor.notifyAll();
        }
    }

    static void queue(MailMessage mailMessage, Class<MailDeliveryCallback> callbackClass, IMailServiceConfigConfiguration mailConfig) {
        final AbstractOutgoingMailQueue persistable = EntityFactory.create(persistableEntities.get(mailConfig.configurationId()));
        if (persistable == null) {
            throw new Error("MailQueue Persistence not configured for '" + mailConfig.configurationId() + "'");
        }
        persistable.status().setValue(MailQueueStatus.Queued);
        persistable.attempts().setValue(0);
        persistable.configurationId().setValue(mailConfig.configurationId());
        persistable.data().setValue(SerializationUtils.serialize(mailMessage));
        if (callbackClass != null) {
            persistable.statusCallbackClass().setValue(callbackClass.getName());
        }

        runInTargetNamespace(persistable, new Callable<Void>() {
            @Override
            public Void call() {
                Persistence.service().persist(persistable);
                return null;
            }
        });

        Persistence.service().addTransactionCompletionHandler(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() throws RuntimeException {
                sendQueued();
                return null;
            }
        });
    }

    @Override
    public void run() {
        do {
            synchronized (monitor) {
                try {
                    monitor.wait(2 * Consts.MIN2MSEC);
                } catch (InterruptedException e) {
                    log.info("MailQueue Interrupted");
                    return;
                }
            }
            try {
                boolean continueDelivery = true;
                do {
                    final AbstractOutgoingMailQueue persistable = peek();
                    if (persistable == null) {
                        continueDelivery = false;
                    } else {
                        final AbstractOutgoingMailQueue persistableUpdate = (AbstractOutgoingMailQueue) EntityFactory.create(persistable.getEntityMeta()
                                .getEntityClass());
                        persistableUpdate.status().setValue(persistable.status().getValue());
                        persistableUpdate.updated().setValue(SystemDateManager.getDate());

                        IMailServiceConfigConfiguration mailConfig = configurations.get(persistable.configurationId().getValue());
                        MailMessage mailMessage = (MailMessage) SerializationUtils.deserialize(persistable.data().getValue());
                        MailDeliveryStatus status = Mail.send(mailMessage, mailConfig);

                        switch (status) {
                        case Success:
                            persistableUpdate.status().setValue(MailQueueStatus.Success);
                            break;
                        case ConfigurationError:
                            continueDelivery = false;
                            break;
                        case ConnectionError:
                            continueDelivery = false;
                            break;
                        case MessageDataError:
                            persistableUpdate.status().setValue(MailQueueStatus.Cancelled);
                            break;
                        }
                        persistableUpdate.attempts().setValue(persistable.attempts().getValue(0) + 1);

                        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                            @Override
                            public Void execute() throws RuntimeException {
                                runInTargetNamespace(persistable, new Callable<Void>() {
                                    @Override
                                    public Void call() {
                                        Persistence.service().update(EntityCriteriaByPK.create(persistable), persistableUpdate);
                                        return null;
                                    }
                                });
                                return null;
                            }
                        });
                    }
                } while (continueDelivery && !shutdown);
            } catch (Throwable t) {
                log.error("MailQueue delivery error", t);
            }
        } while (!shutdown);
    }

    private AbstractOutgoingMailQueue peek() {
        return new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<AbstractOutgoingMailQueue, RuntimeException>() {

            @Override
            public AbstractOutgoingMailQueue execute() throws RuntimeException {
                for (final Class<? extends AbstractOutgoingMailQueue> persistableEntityClass : persistableEntities.values()) {

                    AbstractOutgoingMailQueue persistable = runInTargetNamespace(persistableEntityClass, new Callable<AbstractOutgoingMailQueue>() {
                        @Override
                        public AbstractOutgoingMailQueue call() {
                            @SuppressWarnings("unchecked")
                            EntityListCriteria<AbstractOutgoingMailQueue> criteria = (EntityListCriteria<AbstractOutgoingMailQueue>) EntityListCriteria
                                    .create(persistableEntityClass);
                            criteria.eq(criteria.proto().status(), MailQueueStatus.Queued);
                            criteria.asc(criteria.proto().attempts());
                            criteria.asc(criteria.proto().updated());
                            return Persistence.service().retrieve(criteria);
                        }
                    });

                    if (persistable != null) {
                        return persistable;
                    }
                }
                return null;
            }
        });
    }

    private static String getNamespace(Class<? extends IEntity> entityClass) {
        Table table = entityClass.getAnnotation(Table.class);
        if (table == null) {
            return null;
        } else {
            if (CommonsStringUtils.isStringSet(table.namespace())) {
                return table.namespace();
            } else {
                return null;
            }
        }
    }

    private static <T> T runInTargetNamespace(Class<? extends IEntity> entityClass, final Callable<T> task) {
        String targetNamespace = getNamespace(entityClass);
        if (targetNamespace == null) {
            targetNamespace = NamespaceManager.getNamespace();
        }
        return NamespaceManager.runInTargetNamespace(targetNamespace, task);
    }

    private static <T> T runInTargetNamespace(AbstractOutgoingMailQueue persistable, final Callable<T> task) {
        String targetNamespace = getNamespace(persistable.getEntityMeta().getEntityClass());
        if (targetNamespace == null) {
            targetNamespace = NamespaceManager.getNamespace();
        }
        return NamespaceManager.runInTargetNamespace(targetNamespace, task);
    }
}
