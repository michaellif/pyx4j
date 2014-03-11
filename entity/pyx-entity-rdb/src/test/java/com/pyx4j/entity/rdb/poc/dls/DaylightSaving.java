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
 * Created on Mar 11, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.poc.dls;

import java.util.Calendar;

import junit.framework.Assert;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.rdb.PersistenceEnvironmentFactory;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.server.PersistenceEnvironment;
import com.pyx4j.entity.test.shared.domain.temporal.JustDate;
import com.pyx4j.gwt.server.DateUtils;

public class DaylightSaving extends DatastoreTestBase {

    @Override
    protected PersistenceEnvironment getPersistenceEnvironment() {
        DatabaseType databaseType;

        databaseType = DatabaseType.PostgreSQL;
        //databaseType = DatabaseType.HSQLDB;
        //databaseType = DatabaseType.Oracle;
        //databaseType = DatabaseType.MySQL;
        //databaseType = DatabaseType.Derby;

        return PersistenceEnvironmentFactory.getPersistenceEnvironment(databaseType);
    }

    public void testTimePeriods() {
        // daylight saving time, EDT  2014-03-09 2:00->3:00
        JustDate dlst1 = EntityFactory.create(JustDate.class);
        dlst1.value().setValue(DateUtils.detectDateformat("2014-03-09 06:21:28"));
        srv.persist(dlst1);

        for (int i = 0; i <= 20; i++) {
            JustDate dlst1r = srv.retrieve(JustDate.class, dlst1.getPrimaryKey());
            Calendar c = Calendar.getInstance();
            c.setTime(dlst1r.value().getValue());
            Assert.assertEquals("try# " + i + "; hour of " + dlst1r.value().getStringView(), 6, c.get(Calendar.HOUR_OF_DAY));
        }

    }
}
