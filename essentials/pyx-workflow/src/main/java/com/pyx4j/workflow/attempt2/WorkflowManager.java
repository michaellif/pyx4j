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
 * Created on Jan 14, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.workflow.attempt2;

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.workflow.attempt2.domain.Case;
import com.pyx4j.workflow.attempt2.domain.Token;

public enum WorkflowManager {

    Instance;

    private final Map<String, Process> processes;

    private WorkflowManager() {
        processes = new HashMap<String, Process>();
    }

    public void registerProcess(Process process) {

        process.validate();

        processes.put(process.getId(), process);
    }

    public Case openCase(String processId) {
        Case c = EntityFactory.create(Case.class);
        c.processId().setValue(processId);

        Process process = processes.get(processId);

        Place<?> startPlace = process.getStartPlace();

        Token token = process.createStartToken();
        token.placeId().setValue(startPlace.getPlaceId());

        c.tokens().add(token);

        return c;
    }

    public void triggerCase(Case c) {
    }

    public void closeCase(Case c) {

    }

}
