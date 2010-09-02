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
 * Created on 2010-09-02
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.admin;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.essentials.server.deferred.IDeferredProcess;

public class AdminCronJobsFactory implements CronJobsFactory {

    private static final Logger log = LoggerFactory.getLogger(AdminCronJobsFactory.class);

    public static enum AdminJobs {

        PurgeSessions

    }

    @Override
    public IDeferredProcess createProcess(HttpServletRequest request) {
        AdminJobs job;
        try {
            job = AdminJobs.valueOf(request.getParameter("job"));
        } catch (Throwable e) {
            log.error("unknown admin cron job {}", request.getParameter("job"));
            return null;
        }

        IDeferredProcess process;
        switch (job) {
        case PurgeSessions:
            process = new SessionsPurgeDeferredProcess(false);
            break;
        default:
            log.error("unknown admin cron job {}", job);
            return null;
        }
        return process;
    }

}
