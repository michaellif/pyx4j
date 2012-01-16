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

import java.util.ArrayList;
import java.util.Collection;

import com.pyx4j.workflow.attempt2.domain.Token;

public class Place<T extends Token> {

    private final String id;

    private final Collection<Transition> inputs;

    private final Collection<Transition> outputs;

    public Place(String id) {
        this.id = id;
        inputs = new ArrayList<Transition>();
        outputs = new ArrayList<Transition>();

    }

    public String getPlaceId() {
        return id;
    }

    public void input(Transition transition) {
        inputs.add(transition);
    }

    public void output(Transition transition) {
        outputs.add(transition);
    }

    public boolean isStart() {
        return inputs.size() == 0;
    }

    public boolean isEnd() {
        return outputs.size() == 0;
    }
}
