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
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;

import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityCriteria;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

/**
 * 
 * @see PersistenceServicesFactory.GAE_IMPL_CLASS
 * 
 */
public class EntityPersistenceServiceGAE implements IEntityPersistenceService {

    private static final Logger log = LoggerFactory.getLogger(EntityPersistenceServiceGAE.class);

    private final int ORDINARY_STRING_LENGHT_MAX = 500;

    private final DatastoreService datastore;

    public EntityPersistenceServiceGAE() {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    private void updateEntityProperties(Entity entity, IEntity<?> iEntity) {
        for (Map.Entry<String, Object> me : iEntity.getValue().entrySet()) {
            if (me.getKey().equals(IEntity.PRIMARY_KEY)) {
                continue;
            }
            Object value = me.getValue();
            if (value instanceof Map<?, ?>) {
                String childKey;
                if (iEntity.getMemberMeta(me.getKey()).isOwnedRelationships()) {
                    // Save Owned iEntity
                    IEntity<?> childIEntity = (IEntity<?>) iEntity.getMember(me.getKey());
                    persist(childIEntity);
                    childKey = childIEntity.getPrimaryKey();
                } else {
                    childKey = (String) ((Map<String, Object>) value).get(IEntity.PRIMARY_KEY);
                    if (childKey == null) {
                        continue;
                    }
                }
                value = KeyFactory.stringToKey(childKey);
            } else if (value instanceof String) {
                if (iEntity.getMemberMeta(me.getKey()).getStringLength() > ORDINARY_STRING_LENGHT_MAX) {
                    value = new Text((String) value);
                }
            }
            entity.setProperty(me.getKey(), value);
        }
    }

    private String getIEntityKind(IEntity<?> iEntity) {
        return iEntity.getObjectClass().getName();
    }

    private <T extends IEntity<?>> String getIEntityKind(Class<T> entityClass) {
        return entityClass.getName();
    }

    @Override
    public void persist(IEntity<?> iEntity) {
        Entity entity;
        if (iEntity.getPrimaryKey() == null) {
            entity = new Entity(getIEntityKind(iEntity));
        } else {
            Key key = KeyFactory.stringToKey(iEntity.getPrimaryKey());
            entity = new Entity(key);
        }
        updateEntityProperties(entity, iEntity);
        Key keyCreated = datastore.put(entity);
        iEntity.setPrimaryKey(KeyFactory.keyToString(keyCreated));
    }

    @Override
    public void delete(IEntity<?> iEntity) {
        datastore.delete(KeyFactory.stringToKey(iEntity.getPrimaryKey()));
    }

    private void updateIEntity(IEntity<?> iEntity, Entity entity) {
        iEntity.setPrimaryKey(KeyFactory.keyToString(entity.getKey()));
        for (Map.Entry<String, Object> me : entity.getProperties().entrySet()) {
            Object value = me.getValue();
            if (value instanceof Text) {
                value = ((Text) value).getValue();
            } else if (value instanceof Key) {
                IEntity<?> childIEntity = (IEntity<?>) iEntity.getMember(me.getKey());
                retrieveEntity(childIEntity, (Key) value);
                continue;
            }
            iEntity.setMemberValue(me.getKey(), value);
        }
    }

    private void retrieveEntity(IEntity<?> iEntity, Key key) {
        if (!getIEntityKind(iEntity).equals(key.getKind())) {
            throw new RuntimeException("Unexpected IEntity " + getIEntityKind(iEntity) + " Kind " + key.getKind());
        }
        Entity entity;
        try {
            entity = datastore.get(key);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("EntityNotFound");
        }
        updateIEntity(iEntity, entity);
    }

    @Override
    public <T extends IEntity<?>> T retrieve(Class<T> entityClass, String primaryKey) {
        Key key = KeyFactory.stringToKey(primaryKey);
        if (!getIEntityKind(entityClass).equals(key.getKind())) {
            throw new RuntimeException("Unexpected IEntity " + getIEntityKind(entityClass) + " Kind " + key.getKind());
        }
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

    public static Query.FilterOperator operator(PropertyCriterion.Restriction restriction) {
        switch (restriction) {
        case LESS_THAN:
            return Query.FilterOperator.LESS_THAN;
        case LESS_THAN_OR_EQUAL:
            return Query.FilterOperator.LESS_THAN_OR_EQUAL;
        case GREATER_THAN:
            return Query.FilterOperator.GREATER_THAN;
        case GREATER_THAN_OR_EQUAL:
            return Query.FilterOperator.GREATER_THAN_OR_EQUAL;
        case EQUAL:
            return Query.FilterOperator.EQUAL;
        case NOT_EQUAL:
            return Query.FilterOperator.NOT_EQUAL;
        case IN:
            return Query.FilterOperator.IN;
        default:
            throw new RuntimeException("Unsupported Operator " + restriction);
        }
    }

    private void addFilter(Query query, PropertyCriterion propertyCriterion) {
        query.addFilter(propertyCriterion.getPropertyName(), operator(propertyCriterion.getRestriction()), propertyCriterion.getValue());
    }

    @Override
    public <T extends IEntity<?>> List<T> query(EntityCriteria<T> criteria) {
        Class<T> entityClass;
        try {
            entityClass = (Class<T>) Class.forName(criteria.getDomainName(), true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Not an Entity");
        }
        Query query = new Query(getIEntityKind(entityClass));

        if (criteria.getFilters() != null) {
            for (Criterion cr : criteria.getFilters()) {
                if (cr instanceof PropertyCriterion) {
                    addFilter(query, (PropertyCriterion) cr);
                }
            }
        }

        PreparedQuery pq = datastore.prepare(query);

        List<T> rc = new Vector<T>();
        for (Entity entity : pq.asIterable()) {
            T iEntity = EntityFactory.create(entityClass);
            updateIEntity(iEntity, entity);
            rc.add(iEntity);
        }
        return rc;
    }
}
