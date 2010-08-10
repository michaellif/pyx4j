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

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.appengine.api.datastore.QueryResultIterator;

import com.pyx4j.commons.Consts;
import com.pyx4j.essentials.rpc.admin.BackupRecordsResponse;
import com.pyx4j.essentials.rpc.admin.BackupRequest;
import com.pyx4j.essentials.rpc.admin.BackupServices;

public class BackupServicesImpl implements BackupServices {

    private final static Logger log = LoggerFactory.getLogger(BackupServicesImpl.class);

    private final static int TIME_QUOTA_SEC = 15;

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
                HashMap<String, Object> record = new HashMap<String, Object>();
                record.putAll(entity.getProperties());
                record.put(Entity.KEY_RESERVED_PROPERTY, entity.getKey().getId());
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
