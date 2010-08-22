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
 * Created on Aug 21, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.j2se;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.essentials.rpc.admin.DBMaintenanceRequest;
import com.pyx4j.essentials.rpc.admin.DBMaintenanceServices;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessServices;
import com.pyx4j.rpc.j2se.J2SEService;

public class DBMaintenance {

    private static final Logger log = LoggerFactory.getLogger(DBMaintenance.class);

    public void execute(J2SEService srv, DBMaintenanceRequest request) {

        String deferredCorrelationID = srv.execute(DBMaintenanceServices.DBMaintenance.class, request);

        DeferredProcessProgressResponse response;
        do {
            response = srv.execute(DeferredProcessServices.ContinueExecution.class, deferredCorrelationID);
            log.info("Processed {}", response.getProgress());
        } while (!response.isCompleted());

        log.info("Mainteneace {} of {} rows completed", request.getProcessor().getSimpleName(), response.getProgress());

    }
}
