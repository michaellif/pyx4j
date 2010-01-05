/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistenceServicesFactory {

    private static final Logger log = LoggerFactory.getLogger(PersistenceServicesFactory.class);

    private static IEntityPersistenceService instance;

    public final static String GAE_IMPL_CLASS = "com.pyx4j.entity.gae.EntityPersistenceServiceGAE";

    public final static String SQL_IMPL_CLASS = "com.pyx4j.entity.hibernate.EntityPersistenceServiceHibernate";

    @SuppressWarnings("unchecked")
    public static synchronized IEntityPersistenceService getPersistenceService() {
        if (instance == null) {
            Class<? extends IEntityPersistenceService> serviceClass;
            try {
                serviceClass = (Class<? extends IEntityPersistenceService>) Class.forName(GAE_IMPL_CLASS);
            } catch (ClassNotFoundException e) {
                try {
                    serviceClass = (Class<? extends IEntityPersistenceService>) Class.forName(SQL_IMPL_CLASS);
                } catch (ClassNotFoundException e2) {
                    throw new RuntimeException("PersistenceService not avalable");
                }
            }
            try {
                instance = serviceClass.newInstance();
            } catch (Throwable e) {
                log.error("creating PersistenceService", e);
                throw new RuntimeException("PersistenceService not avalable");
            }
        }
        return instance;
    }
}
