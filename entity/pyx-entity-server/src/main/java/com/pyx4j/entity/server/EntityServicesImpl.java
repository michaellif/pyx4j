/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server;

import java.util.List;
import java.util.Vector;

import com.pyx4j.entity.rpc.EntityServices.Query;
import com.pyx4j.entity.rpc.EntityServices.Save;
import com.pyx4j.entity.shared.EntityCriteria;
import com.pyx4j.entity.shared.IEntity;

public class EntityServicesImpl {

    public static class SaveImpl implements Save {

        @Override
        public IEntity<?> execute(IEntity<?> request) {
            PersistenceServicesFactory.getPersistenceService().persist(request);
            return request;
        }
    }

    public static class QueryImpl implements Query {

        @SuppressWarnings("unchecked")
        @Override
        public Vector execute(EntityCriteria request) {

            List rc = PersistenceServicesFactory.getPersistenceService().query(request);
            if (rc instanceof Vector<?>) {
                return (Vector) rc;
            } else {
                Vector v = new Vector();
                v.addAll(rc);
                return v;
            }
        }

    }
}
