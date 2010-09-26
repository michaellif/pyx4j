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

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import com.pyx4j.essentials.rpc.admin.BackupRecordsResponse;
import com.pyx4j.essentials.rpc.admin.BackupRequest;
import com.pyx4j.essentials.rpc.admin.BackupServices;
import com.pyx4j.essentials.server.admin.BackupServicesImpl;

public class LocalDatastoreBackupReceiver extends AbstractBackupReceiver {

    private final File file;

    private final LocalServiceTestHelper helper;

    public LocalDatastoreBackupReceiver(String fileName) {
        file = new File(fileName);
        if (!file.exists()) {
            throw new Error("File not found " + file.getAbsolutePath());
        }
        LocalDatastoreServiceTestConfig dsConfig = new LocalDatastoreServiceTestConfig();
        dsConfig.setNoStorage(false);
        dsConfig.setBackingStoreLocation(file.getAbsolutePath());

        helper = new LocalServiceTestHelper(dsConfig);
        helper.setUp();
        //LocalDatastoreServiceTestConfig.getLocalDatastoreService().start();
    }

    @Override
    public void start() {
        // TODO Auto-generated method stub
    }

    @Override
    public void end() {
        LocalDatastoreServiceTestConfig.getLocalDatastoreService().stop();
        helper.tearDown();
    }

    @Override
    protected BackupRecordsResponse get(BackupRequest request) {
        BackupServices.Get get = new BackupServicesImpl.GetImpl();
        return get.execute(request);
    }

}
