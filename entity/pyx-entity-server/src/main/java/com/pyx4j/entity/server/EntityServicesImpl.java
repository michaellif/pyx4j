/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server;

import com.pyx4j.entity.rpc.EntityServices.Save;
import com.pyx4j.entity.shared.IEntity;

public class EntityServicesImpl {

    public static class SaveImpl implements Save {

        @Override
        public IEntity<?> execute(IEntity<?> request) {
            PersistenceServicesFactory.getPersistenceService().persist(request);
            return request;
        }
    }
}
