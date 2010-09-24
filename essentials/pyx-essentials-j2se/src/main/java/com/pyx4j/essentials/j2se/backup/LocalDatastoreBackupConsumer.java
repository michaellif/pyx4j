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
 * Created on 2010-08-12
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.j2se.backup;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import com.pyx4j.essentials.rpc.admin.BackupEntityProperty;
import com.pyx4j.essentials.rpc.admin.BackupServices;
import com.pyx4j.essentials.server.admin.BackupServicesImpl;

public class LocalDatastoreBackupConsumer implements BackupConsumer {

    private static final Logger log = LoggerFactory.getLogger(LocalDatastoreBackupConsumer.class);

    private final File file;

    private final LocalServiceTestHelper helper;

    public long totalRecords;

    public LocalDatastoreBackupConsumer(String fileName, boolean overrride, Date backupDate) {
        fileName = makeFileName(fileName, backupDate);
        file = new File(fileName);
        if (overrride && file.exists()) {
            file.delete();
        }
        LocalDatastoreServiceTestConfig dsConfig = new LocalDatastoreServiceTestConfig();
        dsConfig.setNoStorage(false);
        dsConfig.setBackingStoreLocation(fileName);

        helper = new LocalServiceTestHelper(dsConfig);
        helper.setUp();
        //LocalDatastoreServiceTestConfig.getLocalDatastoreService().start();
    }

    protected String makeFileName(String fileName, Date backupDate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat tf = new SimpleDateFormat("HH-mm");
        return fileName.replace("(date)", df.format(backupDate)).replace("(time)", tf.format(backupDate));
    }

    @Override
    public void start() {
        // TODO Auto-generated method stub
    }

    @Override
    public void save(Vector<HashMap<String, BackupEntityProperty>> records) {
        boolean ok = false;
        try {
            BackupServices.Put put = new BackupServicesImpl.PutImpl();
            put.execute(records);
            ok = true;
            totalRecords += records.size();
        } finally {
            if (!ok) {
                log.error("error saving records {}", records.get(0));
            }
        }
    }

    @Override
    public void end() {
        LocalDatastoreServiceTestConfig.getLocalDatastoreService().stop();
        helper.tearDown();
        log.info("Saved {} records to {}", totalRecords, file.getAbsolutePath());
    }

}
