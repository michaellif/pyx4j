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

import com.pyx4j.essentials.rpc.admin.BackupRecordsResponse;
import com.pyx4j.essentials.rpc.admin.BackupRequest;
import com.pyx4j.essentials.rpc.admin.BackupServices;
import com.pyx4j.rpc.j2se.J2SEService;

public class ServerBackupReceiver extends AbstractBackupReceiver {

    protected J2SEService srv;

    public ServerBackupReceiver(J2SEService srv) {
        this.srv = srv;
    }

    @Override
    public void start() {
    }

    @Override
    public void end() {
        srv.logout();
    }

    @Override
    public void completed() {
        String report = createReport();
        if (report != null) {
            srv.execute(BackupServices.BackupReport.class, report);
        }
    }

    protected String createReport() {
        return null;
    }

    @Override
    protected BackupRecordsResponse get(BackupRequest request) {
        return srv.execute(BackupServices.Get.class, request);
    }

}
