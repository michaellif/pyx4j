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
 * Created on Jan 31, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.http.client.Response;

public class AppEngineUsage {

    private static final Logger log = LoggerFactory.getLogger(AppEngineUsage.class);

    private static double estimatedDollars = 0;

    /**
     * Process headers from GAE.
     * 
     * Examples.
     * 
     * X-AppEngine-Estimated-CPM-US-Dollars: $0.142024
     * 
     * X-AppEngine-Resource-Usage: ms=5097 cpu_ms=6122 api_cpu_ms=126
     */
    static public void onResponseReceived(Response response) {
        String d = response.getHeader("X-AppEngine-Estimated-CPM-US-Dollars");
        if ((d != null) && (d.startsWith("$"))) {
            addEstimatedDollars(d.substring(1));
        }
        //TODO
        //String u = response.getHeader("X-AppEngine-Resource-Usage");
    }

    public static double getEstimatedDollars() {
        return estimatedDollars;
    }

    public static void addEstimatedDollars(String estimatedDollars) {
        try {
            AppEngineUsage.estimatedDollars += Double.valueOf(estimatedDollars) / 1000;
            log.info("AppEngine estimated usage per 1000 requests {}$; This Session total {}$", estimatedDollars, AppEngineUsage.estimatedDollars);
        } catch (NumberFormatException e) {
            log.error("pars error", e);
        }
    }
}
