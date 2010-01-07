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

import com.pyx4j.entity.shared.EntityCriteria;
import com.pyx4j.entity.shared.IEntity;

public interface IEntityPersistenceService {

    public void persist(IEntity<?> entity);

    public <T extends IEntity<?>> T retrieve(Class<T> entityClass, String primaryKey);

    public <T extends IEntity<?>> List<T> query(EntityCriteria<T> criteria);

    public void delete(IEntity<?> entity);

}
