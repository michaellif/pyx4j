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
 */
package com.pyx4j.server.mail;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Consts;
import com.pyx4j.commons.ConverterUtils;
import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Executables;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.AbstractOutgoingMailQueue;
import com.pyx4j.entity.shared.AbstractOutgoingMailQueue.MailQueueStatus;
import com.pyx4j.entity.shared.utils.EntityFormatUtils;
import com.pyx4j.log4j.LoggerConfig;
import com.pyx4j.server.contexts.NamespaceManager;

//TODO re-read configuration upon errors
//TODO Cleanup old records
//TODO reuse SMTP session for perfomance
//TODO separate delivery wait for different configurations
//TODO add Throttling support, e.g.  2000 messages per day
public class MailQueue implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(MailQueue.class);

    private static Map<String, Class<? extends AbstractOutgoingMailQueue>> persistableEntities = new HashMap<>();

    private static Map<String, IMailServiceConfigConfiguration> configurations = new HashMap<>();

    private static MailQueue instance;

    private final Object monitor = new Object();

    private volatile boolean shutdown = false;

    private volatile boolean running = false;

    private MailQueue() {
    }

    public static void initialize(IMailServiceConfigConfiguration config, Class<? extends AbstractOutgoingMailQueue> persistableEntityClass) {
        persistableEntities.put(config.configurationId(), persistableEntityClass);
        configurations.put(config.configurationId(), config);
    }

    private static synchronized void start() {
        if (instance == null) {
            instance = new MailQueue();
            Thread caseWatch = new Thread(instance, LoggerConfig.getContextName() + "_MailQueueDeliveryThread");
            caseWatch.setDaemon(true);
            caseWatch.start();
        }
    }

    public static synchronized void setActive(boolean active) {
        if (active) {
            if (!isRunning()) {
                start();
            }
        } else {
            shutdown();
        }
    }

    public static void shutdown() {
        if (instance != null) {
            instance.notifyAndWaitCompletion();
            instance = null;
        }
    }

    private void notifyAndWaitCompletion() {
        shutdown = true;
        synchronized (monitor) {
            monitor.notifyAll();
        }
        while (running) {
            synchronized (monitor) {
                try {
                    monitor.wait(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    public static void sendQueued() {
        if (instance != null) {
            synchronized (instance.monitor) {
                instance.monitor.notifyAll();
            }
        }
    }

    public static boolean isRunning() {
        if (instance != null) {
            return instance.running;
        } else {
            return false;
        }
    }

    static void queue(MailMessage mailMessage, Class<? extends MailDeliveryCallback> callbackClass, IMailServiceConfigConfiguration mailConfig) {
        Class<? extends AbstractOutgoingMailQueue> persistableClass = persistableEntities.get(mailConfig.configurationId());
        if (persistableClass == null) {
            persistableClass = ((SMTPMailServiceConfig) mailConfig).persistableQueueEntityClass();
        }
        final AbstractOutgoingMailQueue persistable = EntityFactory.create(persistableClass);
        if (persistable == null) {
            throw new Error("MailQueue Persistence not configured for '" + mailConfig.configurationId() + "'");
        }
        persistable.namespace().setValue(NamespaceManager.getNamespace());
        persistable.status().setValue(MailQueueStatus.Queued);
        persistable.attempts().setValue(0);
        persistable.priority().setValue(mailConfig.queuePriority());
        persistable.configurationId().setValue(mailConfig.configurationId());
        persistable.data().setValue(SerializationUtils.serialize(mailMessage));
        if (callbackClass != null) {
            persistable.statusCallbackClass().setValue(callbackClass.getName());
        }
        persistable.sender().setValue(mailMessage.getSender());
        Collection<String> sendTo = CollectionUtils.union(CollectionUtils.union(mailMessage.getTo(), mailMessage.getCc()), mailMessage.getBcc());
        if (sendTo.isEmpty()) {
            if (((SMTPMailServiceConfig) mailConfig).queueUndeliverable()) {
                persistable.status().setValue(MailQueueStatus.Undeliverable);
            } else if (mailMessage.getKeywords().contains("bulk")) {
                log.debug("No destination E-Mail addresses found in bulk, message delivery canceled");
                persistable.status().setValue(MailQueueStatus.Cancelled);
            } else {
                throw new Error("No destination E-Mail addresses found");
            }
        }
        persistable.sendTo().setValue(ConverterUtils.convertStringCollection(sendTo, ", "));
        EntityFormatUtils.trimToLength(persistable.sendTo());
        persistable.keywords().setValue(ConverterUtils.convertStringCollection(mailMessage.getKeywords(), ", "));

        Executables.wrapInEntityNamespace(persistable.getEntityMeta().getEntityClass(), new Executable<Void, RuntimeException>() {
            @Override
            public Void execute() {
                Persistence.service().persist(persistable);
                return null;
            }
        }).execute();

        if (isRunning()) {
            // Transaction is actually completed, wake up the delivery thread.
            Persistence.service().addTransactionCompletionHandler(new Executable<Void, RuntimeException>() {

                @Override
                public Void execute() {
                    sendQueued();
                    return null;
                }
            });
        } else {
            log.warn("MailQueue is not running, email will be delivered after next application restart");
        }
    }

    @Override
    public void run() {
        try {
            running = true;
            log.info("MailQueue started");
            int deliveryErrorCount = 0;
            int mailQueueEmptyCount = 0;
            do {

                long sleepMinutes = 2; // default upon application startup
                if (deliveryErrorCount > 0) {
                    // Slow down retry attempts if there are communication outage
                    if (deliveryErrorCount > 10) { // 20 minutes outage
                        sleepMinutes = 10;
                    } else if (deliveryErrorCount > 26) { // 2 hours outage (180m = 2m * 10 + 10m * 16)
                        sleepMinutes = 60;
                    }
                } else if (mailQueueEmptyCount > 10) {
                    // There should be no way to put records in to DB Queue without calling notification, in any case we will check it every now and then.
                    sleepMinutes = 30;
                }

                // Wait for sendQueued notifications or shutdown
                synchronized (monitor) {
                    try {
                        monitor.wait(sleepMinutes * Consts.MIN2MSEC);
                    } catch (InterruptedException e) {
                        log.info("MailQueue Interrupted");
                        return;
                    }
                }
                // Do try not to make delivery upon shutdown or System Maintenance.
                if (shutdown || ServerSideConfiguration.instance().datastoreReadOnly()) {
                    break;
                }

                try {
                    boolean continueDelivery = true;
                    do {
                        final AbstractOutgoingMailQueue persistable = peek();
                        if (persistable == null) {
                            continueDelivery = false;
                            mailQueueEmptyCount++;
                        } else {
                            mailQueueEmptyCount = 0;
                            final AbstractOutgoingMailQueue persistableUpdate = (AbstractOutgoingMailQueue) EntityFactory
                                    .create(persistable.getEntityMeta().getEntityClass());
                            persistableUpdate.status().setValue(persistable.status().getValue());
                            persistableUpdate.updated().setValue(SystemDateManager.getDate());

                            final MailMessage mailMessage = (MailMessage) SerializationUtils.deserialize(persistable.data().getValue());

                            IMailServiceConfigConfiguration mailConfig = configurations.get(persistable.configurationId().getValue());
                            if (mailConfig == null) {
                                mailConfig = ServerSideConfiguration.instance().getMailServiceConfigConfiguration();
                            }
                            if (mailConfig instanceof SMTPMailServiceConfig) {
                                final SMTPMailServiceConfig origConfig = (SMTPMailServiceConfig) mailConfig;
                                Executable<IMailServiceConfigConfiguration, RuntimeException> selectConfiguration = new Executable<IMailServiceConfigConfiguration, RuntimeException>() {
                                    @Override
                                    public IMailServiceConfigConfiguration execute() {
                                        return origConfig.selectConfigurationInstance(mailMessage);
                                    }
                                };

                                if (persistable.namespace().isNull()) {
                                    mailConfig = selectConfiguration.execute();
                                } else {
                                    mailConfig = Executables.runInTargetNamespace(persistable.namespace().getValue(), selectConfiguration);
                                }
                            }
                            MailDeliveryStatus status = null;
                            if (persistable.namespace().isNull()) {
                                status = Mail.send(mailMessage, mailConfig);
                            } else {
                                final IMailServiceConfigConfiguration targetConfig = mailConfig;
                                status = Executables.runInTargetNamespace( //
                                        persistable.namespace().getValue(), //
                                        new Executable<MailDeliveryStatus, RuntimeException>() {

                                            @Override
                                            public MailDeliveryStatus execute() throws RuntimeException {
                                                return Mail.send(mailMessage, targetConfig);
                                            }
                                        });
                            }

                            switch (status) {
                            case Success:
                                persistableUpdate.status().setValue(MailQueueStatus.Success);
                                persistableUpdate.messageId().setValue(mailMessage.getHeader("Message-ID"));
                                persistableUpdate.sentDate().setValue(mailMessage.getHeader("Date"));
                                deliveryErrorCount = 0;
                                log.debug("message {} was sent; mailConfig {}", persistable.getPrimaryKey(), mailConfig.configurationId());
                                break;
                            case ConfigurationError:
                                continueDelivery = false;
                                deliveryErrorCount++;
                                log.error("message {} was not sent because of the configuration error, administrator review required ; mailConfig {}",
                                        persistable.getPrimaryKey(), mailConfig.configurationId());
                                break;
                            case ConnectionError:
                                continueDelivery = false;
                                deliveryErrorCount++;
                                log.debug("message {} was not sent; mailConfig {}", persistable.getPrimaryKey(), mailConfig.configurationId());
                                break;
                            case MessageDataError:
                                persistableUpdate.status().setValue(MailQueueStatus.Cancelled);
                                log.warn("message {} was not sent because of the data problem, delivery attempts Cancelled", persistable.getPrimaryKey());
                                break;
                            }
                            persistableUpdate.lastAttemptErrorMessage().setValue(
                                    trunkLength(mailMessage.getDeliveryErrorMessage(), persistableUpdate.lastAttemptErrorMessage().getMeta().getLength()));
                            EntityFormatUtils.trimToLength(persistableUpdate.lastAttemptErrorMessage());
                            persistableUpdate.attempts().setValue(persistable.attempts().getValue(0) + 1);
                            persistableUpdate.sender().setValue(mailMessage.getSender());
                            if ((persistable.attempts().getValue() > mailConfig.maxDeliveryAttempts())
                                    && (persistableUpdate.status().getValue() != MailQueueStatus.Success)) {
                                persistableUpdate.status().setValue(MailQueueStatus.GiveUp);
                                log.warn("message {} was not sent, delivery attempts stoppered; mailConfig {}", persistable.getPrimaryKey(),
                                        mailConfig.configurationId());
                            }
                            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                                @Override
                                public Void execute() {
                                    Executables.wrapInEntityNamespace(persistable.getEntityMeta().getEntityClass(), new Executable<Void, RuntimeException>() {
                                        @Override
                                        public Void execute() {
                                            Persistence.service().update(EntityCriteriaByPK.create(persistable), persistableUpdate);
                                            return null;
                                        }
                                    }).execute();
                                    return null;
                                }
                            });
                            if (!persistable.statusCallbackClass().isNull()) {
                                invokeCallback(persistable.statusCallbackClass().getValue(), persistable.namespace().getValue(), mailMessage, status,
                                        persistableUpdate.status().getValue(), persistable.attempts().getValue(0));
                            }
                        }
                    } while (continueDelivery && !shutdown);
                } catch (Throwable t) {
                    log.error("MailQueue delivery implementation error", t);
                    // Stop all attempts to send Message
                    return;
                }
            } while (!shutdown);
        } finally {
            running = false;
            synchronized (monitor) {
                monitor.notifyAll();
            }
            log.info("MailQueue stopped");
        }
    }

    private String trunkLength(String message, int length) {
        if ((message != null) && (message.length() > length)) {
            return message.substring(0, length);
        } else {
            return message;
        }
    }

    private void invokeCallback(String statusCallbackClass, String targetNamespace, final MailMessage mailMessage, final MailDeliveryStatus status,
            final MailQueueStatus mailQueueStatus, final int deliveryAttemptsMade) {
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
                    callback.onDeliveryCompleted(mailMessage, status, mailQueueStatus, deliveryAttemptsMade);
                } catch (Throwable e) {
                    log.error("Mail statusCallback invocation error", e);
                }
                return null;
            }
        };

        if (targetNamespace == null) {
            call.execute();
        } else {
            Executables.runInTargetNamespace(targetNamespace, call);
        }
    }

    private AbstractOutgoingMailQueue peek() {
        try {
            return new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<AbstractOutgoingMailQueue, RuntimeException>() {

                @Override
                public AbstractOutgoingMailQueue execute() {
                    for (final Class<? extends AbstractOutgoingMailQueue> persistableEntityClass : persistableEntities.values()) {

                        AbstractOutgoingMailQueue persistable = Executables
                                .wrapInEntityNamespace(persistableEntityClass, new Executable<AbstractOutgoingMailQueue, RuntimeException>() {
                            @Override
                            public AbstractOutgoingMailQueue execute() {
                                @SuppressWarnings("unchecked")
                                EntityListCriteria<AbstractOutgoingMailQueue> criteria = (EntityListCriteria<AbstractOutgoingMailQueue>) EntityListCriteria
                                        .create(persistableEntityClass);
                                criteria.eq(criteria.proto().status(), MailQueueStatus.Queued);
                                criteria.asc(criteria.proto().attempts());
                                criteria.desc(criteria.proto().priority());
                                criteria.asc(criteria.proto().updated());
                                return Persistence.service().retrieve(criteria);
                            }
                        }).execute();

                        if (persistable != null) {
                            return persistable;
                        }
                    }
                    return null;
                }
            });
        } catch (Throwable e) {
            log.error("unable to read MailQueue tables", e);
            return null;
        }
    }

}
