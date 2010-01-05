/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.gae;

import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;

/**
 * 
 * @see PersistenceServicesFactory.GAE_IMPL_CLASS
 * 
 */
public class EntityPersistenceServiceGAE implements IEntityPersistenceService {

    private final DatastoreService datastore;

    public EntityPersistenceServiceGAE() {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    private void updateEntityProperties(Entity entity, IEntity<?> iEntity) {
        for (Map.Entry<String, Object> me : iEntity.getValue().entrySet()) {
            entity.setProperty(me.getKey(), me.getValue());
        }
    }

    private void updateIEntity(IEntity<?> iEntity, Entity entity) {
        iEntity.setValue(entity.getProperties());
    }

    @Override
    public void persist(IEntity<?> iEntity) {
        Entity entity;
        if (iEntity.getPrimaryKey() == null) {
            entity = new Entity(iEntity.getObjectClass().getName());
        } else {
            Key key = KeyFactory.stringToKey(iEntity.getPrimaryKey());
            entity = new Entity(key);
        }
        updateEntityProperties(entity, iEntity);
        Key keyCreated = datastore.put(entity);
        iEntity.setPrimaryKey(KeyFactory.keyToString(keyCreated));
    }

    @Override
    public void delete(IEntity<?> entity) {
        // TODO Auto-generated method stub
    }

    @Override
    public <T extends IEntity<?>> T retrieve(Class<T> entityClass, String primaryKey) {
        Key key = KeyFactory.stringToKey(primaryKey);
        Entity entity;
        try {
            entity = datastore.get(key);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("EntityNotFound");
        }

        T iEntity = EntityFactory.create(entityClass);
        updateIEntity(iEntity, entity);
        return iEntity;
    }

    @Override
    public <T extends IEntity<?>> List<T> query(Class<T> entityClass, Map<String, Object> simpleCriteria) {
        // TODO Auto-generated method stub
        return null;
    }

}
