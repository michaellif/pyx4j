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

import com.pyx4j.workflow.attempt2.domain.Token;

public class Transition {

    public static enum TriggerType {
        Automatic, Event
    }

    private final String id;

    private final Task task;

    private final TriggerType triggerType;

    private final Map<Class<? extends Token>, Arc<?>> inputs;

    private final Map<Class<? extends Token>, Arc<?>> outputs;

    public Transition(String id, Task task, TriggerType triggerType) {
        this.id = id;
        this.task = task;
        this.triggerType = triggerType;
        inputs = new HashMap<Class<? extends Token>, Arc<?>>();
        outputs = new HashMap<Class<? extends Token>, Arc<?>>();
    }

    public <T extends Token> void input(Class<T> tokenType, Place<T> place, Condition<T> condition) {
        inputs.put(tokenType, new Arc<T>(place, condition));
        place.output(this);
    }

    public <T extends Token> void output(Class<T> tokenType, Place<T> place, Condition<T> condition) {
        outputs.put(tokenType, new Arc<T>(place, condition));
        place.input(this);
    }

}
