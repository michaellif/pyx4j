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

import com.pyx4j.workflow.attempt2.Transition.TriggerType;
import com.pyx4j.workflow.attempt2.domain.Token;

public class Process {

    private final String id;

    private final Map<String, Place<?>> places;

    private final Map<String, Transition> transitions;

    public Process(String id) {
        this.id = id;
        places = new HashMap<String, Place<?>>();
        transitions = new HashMap<String, Transition>();
    }

    public String getId() {
        return id;
    }

    public <T extends Token> Place<T> registerPlace(String id) {
        Place<T> place = new Place<T>(id);
        places.put(id, place);
        return place;
    }

    public Transition registerTransition(String id, Task task, TriggerType triggerType) {
        Transition transition = new Transition(id, task, triggerType);
        transitions.put(id, transition);
        return transition;
    }

    public Place<?> getStartPlace() {
        for (Place<?> place : places.values()) {
            if (place.isStart()) {
                return place;
            }
        }
        throw new Error("No start place");
    }

    public Place<?> getEndPlace() {
        for (Place<?> place : places.values()) {
            if (place.isEnd()) {
                return place;
            }
        }
        throw new Error("No end place");
    }

    public void validate() {
        boolean startPlace = false;
        boolean endPlace = false;
        for (Place<?> place : places.values()) {
            if (!startPlace && place.isStart()) {
                startPlace = true;
            } else if (startPlace && place.isStart()) {
                throw new Error("More than one start");
            }
            if (!endPlace && place.isEnd()) {
                endPlace = true;
            } else if (endPlace && place.isEnd()) {
                throw new Error("More than one end");
            }

        }
    }

}
