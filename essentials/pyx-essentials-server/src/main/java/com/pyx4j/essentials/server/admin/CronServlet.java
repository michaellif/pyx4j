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
 * Created on May 22, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.essentials.server.deferred.DeferredProcessTaskWorkerServlet;
import com.pyx4j.essentials.server.deferred.IDeferredProcess;

@SuppressWarnings("serial")
public class CronServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(CronServlet.class);

    protected CronJobsFactory getCronJobsFactory() {
        return new AdminCronJobsFactory();
    }

    protected IDeferredProcess createProcess(HttpServletRequest request) {
        CronJobsFactory factory = getCronJobsFactory();
        return factory.createProcess(request);
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            IDeferredProcess process = createProcess(request);
            if (process == null) {
                log.error("unknown cron job {}", request.getQueryString());
                response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
                return;
            }
            DeferredProcessTaskWorkerServlet.defer(process);
        } catch (Throwable e) {
            log.error("cron error", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
