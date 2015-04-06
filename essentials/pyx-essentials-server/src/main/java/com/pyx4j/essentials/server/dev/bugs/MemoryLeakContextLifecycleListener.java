/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2013-05-13
 * @author vlads
 */
package com.pyx4j.essentials.server.dev.bugs;

import com.pyx4j.config.server.LifecycleListener;

/**
 * Clean all the probable memory leaks to help CI server and profiling sessions
 */
public class MemoryLeakContextLifecycleListener implements LifecycleListener {

    @Override
    public void onRequestBegin() {
    }

    @Override
    public void onRequestEnd() {
    }

    @Override
    public void onRequestError() {
    }

    @Override
    public void onContextEnd() {
        Xmlbeans.fixMemoryLeaks();
    }

}
