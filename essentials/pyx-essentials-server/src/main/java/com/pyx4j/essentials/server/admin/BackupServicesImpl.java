/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on 2010-08-10
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.admin;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.datastore.Text;

import com.pyx4j.commons.Consts;
import com.pyx4j.essentials.rpc.admin.BackupEntityProperty;
import com.pyx4j.essentials.rpc.admin.BackupKey;
import com.pyx4j.essentials.rpc.admin.BackupRecordsResponse;
import com.pyx4j.essentials.rpc.admin.BackupRequest;
import com.pyx4j.essentials.rpc.admin.BackupServices;
import com.pyx4j.geo.GeoPoint;

public class BackupServicesImpl implements BackupServices {

    private final static Logger log = LoggerFactory.getLogger(BackupServicesImpl.class);

    private final static int TIME_QUOTA_SEC = 15;

    public static BackupKey key(Key key) {
        BackupKey bk = new BackupKey();
        bk.setKind(key.getKind());
        if (key.getId() != 0L) {
            bk.setId(key.getId());
        } else {
            bk.setName(key.getName());
        }
        return bk;
    }

    @SuppressWarnings("rawtypes")
    public static Serializable serializeValue(Object value) {
        if ((value instanceof String) || (value instanceof Long) || (value instanceof Date)) {
            return (Serializable) value;
        } else if (value instanceof Text) {
            return ((Text) value).getValue();
        } else if (value instanceof Key) {
            return key((Key) value);
        } else if (value instanceof Blob) {
            //TODO support more types.
            return ((Blob) value).getBytes();
        } else if (value instanceof Collection) {
            Vector<Serializable> r = new Vector<Serializable>();
            for (Object item : (Collection) value) {
                r.add(serializeValue(item));
            }
            return r;
        } else if (value instanceof GeoPt) {
            return new GeoPoint(((GeoPt) value).getLatitude(), ((GeoPt) value).getLongitude());
        }
        return (Serializable) value;
    }

    public static class GetImpl implements BackupServices.Get {

        @Override
        public BackupRecordsResponse execute(BackupRequest request) {
            long start = System.currentTimeMillis();
            BackupRecordsResponse response = new BackupRecordsResponse();

            Query query = new Query(request.getEntityKind());

            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

            PreparedQuery preparedQuery = datastore.prepare(query);
            QueryResultIterable<Entity> iterable;
            if (request.getEncodedCursorRefference() != null) {
                iterable = preparedQuery.asQueryResultIterable(FetchOptions.Builder.withCursor(Cursor.fromWebSafeString(request.getEncodedCursorRefference())));
            } else {
                iterable = preparedQuery.asQueryResultIterable();
            }
            QueryResultIterator<Entity> iterator = iterable.iterator();

            while (iterator.hasNext()) {
                Entity entity = iterator.next();
                HashMap<String, BackupEntityProperty> record = new HashMap<String, BackupEntityProperty>();
                for (Map.Entry<String, Object> me : entity.getProperties().entrySet()) {
                    String propertyName = me.getKey();
                    record.put(propertyName, new BackupEntityProperty(serializeValue(me.getValue()), entity.isUnindexedProperty(propertyName)));
                }
                record.put(Entity.KEY_RESERVED_PROPERTY, new BackupEntityProperty(key(entity.getKey()), false));
                response.addRecord(record);
                boolean quotaExceeded = System.currentTimeMillis() - start > Consts.SEC2MSEC * TIME_QUOTA_SEC;
                if ((response.size() > request.getResponceSize()) || (quotaExceeded)) {
                    response.setEncodedCursorRefference(iterator.getCursor().toWebSafeString());
                    log.warn("Executions time quota exceeded {}", System.currentTimeMillis() - start);
                    break;
                }
            }
            return response;
        }
    }
}
