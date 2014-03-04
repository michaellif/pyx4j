/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2012-08-16
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.gwt.server.deferred;

import java.util.concurrent.atomic.AtomicInteger;

import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;

public abstract class AbstractDeferredProcess implements IDeferredProcess {

    private static final long serialVersionUID = -7013680464201337453L;

    public static class RunningProcess {

        public final AtomicInteger progress = new AtomicInteger();

        public final AtomicInteger progressMaximum = new AtomicInteger();

    }

    protected volatile boolean completed = false;

    //TODO use AtomicBoolean
    protected volatile boolean canceled = false;

    //TODO Use,this
    protected final RunningProcess progress = new RunningProcess();

    @Override
    public void cancel() {
        canceled = true;
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse r = new DeferredProcessProgressResponse();
        r.setProgress(progress.progress.get());
        r.setProgressMaximum(progress.progressMaximum.get());
        if (completed) {
            r.setCompleted();
        } else if (canceled) {
            r.setCanceled();
        }
        return r;

    }
}
