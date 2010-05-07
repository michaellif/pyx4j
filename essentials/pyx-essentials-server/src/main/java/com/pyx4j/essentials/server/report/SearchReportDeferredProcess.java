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
 * Created on 2010-05-07
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.report;

import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.server.deferred.IDeferredProcess;

public class SearchReportDeferredProcess implements IDeferredProcess {

    private static final long serialVersionUID = -7944873735643401186L;

    int test = 1;

    public SearchReportDeferredProcess(EntitySearchCriteria<?> request) {

    }

    @Override
    public void cancel() {
        // TODO Auto-generated method stub
    }

    @Override
    public void execute() {
        //TODO implement this
        if (test > 5) {
            test = 0;
        } else {
            test++;
        }
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse r = new DeferredProcessProgressResponse();
        if (test == 0) {
            r.setCompleted();
        } else {
            r.setProgress(test);
        }
        return r;
    }

}
