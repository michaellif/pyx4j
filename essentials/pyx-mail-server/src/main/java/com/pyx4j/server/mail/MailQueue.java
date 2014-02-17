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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Consts;
import com.pyx4j.commons.ConverterUtils;
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
        persistable.namespace().setValue(NamespaceManager.getNamespace());
        persistable.status().setValue(MailQueueStatus.Queued);
        persistable.attempts().setValue(0);
        persistable.configurationId().setValue(mailConfig.configurationId());
        persistable.data().setValue(SerializationUtils.serialize(mailMessage));
        if (callbackClass != null) {
            persistable.statusCallbackClass().setValue(callbackClass.getName());
        }
        Collection<String> sendTo = CollectionUtils.union(CollectionUtils.union(mailMessage.getTo(), mailMessage.getCc()), mailMessage.getBcc());
        persistable.sendTo().setValue(ConverterUtils.convertStringCollection(sendTo, ", "));
        persistable.keywords().setValue(ConverterUtils.convertStringCollection(mailMessage.getKeywords(), ", "));

        runInEntityNamespace(persistable.getEntityMeta().getEntityClass(), new Executable<Void, RuntimeException>() {
            @Override
            public Void execute() {
                Persistence.service().persist(persistable);
                return null;
            }
        });

        Persistence.service().addTransactionCompletionHandler(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() {
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
                            persistableUpdate.messageId().setValue(mailMessage.getHeader("Message-ID"));
                            persistableUpdate.sentDate().setValue(mailMessage.getHeader("Date"));
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
                        if (persistable.attempts().getValue() > 40) {
                            persistableUpdate.status().setValue(MailQueueStatus.GiveUp);
                        }
                        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                            @Override
                            public Void execute() {
                                runInEntityNamespace(persistable.getEntityMeta().getEntityClass(), new Executable<Void, RuntimeException>() {
                                    @Override
                                    public Void execute() {
                                        Persistence.service().update(EntityCriteriaByPK.create(persistable), persistableUpdate);
                                        return null;
                                    }
                                });
                                return null;
                            }
                        });
                        if (!persistable.statusCallbackClass().isNull()) {
                            invokeCallback(persistable.statusCallbackClass().getValue(), persistable.namespace().getValue(), mailMessage, status);
                        }
                    }
                } while (continueDelivery && !shutdown);
            } catch (Throwable t) {
                log.error("MailQueue delivery error", t);
            }
        } while (!shutdown);
    }

    private void invokeCallback(String statusCallbackClass, String targetNamespace, final MailMessage mailMessage, final MailDeliveryStatus status) {
        Class<?> callbackClass;
        try {
            callbackClass = Thread.currentThread().getContextClassLoader().loadClass(statusCallbackClass);
        } catch (ClassNotFoundException e) {
            log.error("Error loading Mail statusCallback Class", e);
            return;
        }
        final MailDeliveryCallback callback;
        try {
            callback = (MailDeliveryCallback) callbackClass.newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassCastException e) {
            log.error("Error loading Mail statusCallback Class", e);
            return;
        }

        Executable<Void, RuntimeException> call = new Executable<Void, RuntimeException>() {
            @Override
            public Void execute() {
                try {
                    callback.onDeliveryCompleted(mailMessage, status);
                } catch (Throwable e) {
                    log.error("Mail statusCallback invocation error", e);
                }
                return null;
            }
        };

        if (targetNamespace == null) {
            call.execute();
        } else {
            NamespaceManager.runInTargetNamespace(targetNamespace, call);
        }
    }

    private AbstractOutgoingMailQueue peek() {
        return new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<AbstractOutgoingMailQueue, RuntimeException>() {

            @Override
            public AbstractOutgoingMailQueue execute() {
                for (final Class<? extends AbstractOutgoingMailQueue> persistableEntityClass : persistableEntities.values()) {

                    AbstractOutgoingMailQueue persistable = runInEntityNamespace(persistableEntityClass,
                            new Executable<AbstractOutgoingMailQueue, RuntimeException>() {
                                @Override
                                public AbstractOutgoingMailQueue execute() {
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
        Class<? extends IEntity> persistableEntityClass = EntityFactory.getEntityMeta(entityClass).getPersistableSuperClass();
        if (persistableEntityClass != null) {
            entityClass = persistableEntityClass;
        }
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

    private static <T> T runInEntityNamespace(Class<? extends IEntity> entityClass, final Executable<T, RuntimeException> task) {
        String targetNamespace = getNamespace(entityClass);
        if (targetNamespace != null) {
            return NamespaceManager.runInTargetNamespace(targetNamespace, task);
        } else {
            return task.execute();
        }
    }

}
