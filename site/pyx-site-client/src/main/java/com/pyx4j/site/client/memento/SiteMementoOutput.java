/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Nov 7, 2014
 * @author michaellif
 */
package com.pyx4j.site.client.memento;

import java.util.Iterator;
import java.util.List;

import com.pyx4j.widgets.client.memento.IMementoInput;

public class SiteMementoOutput implements IMementoInput {

    private final List<?> state;

    private Iterator<?> iterator;

    SiteMementoOutput(List<?> state) {
        this.state = state;
        if (state != null) {
            iterator = state.iterator();
        }
    }

    @Override
    public Object read() {
        if (iterator != null && iterator.hasNext()) {
            return iterator.next();
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return state == null ? null : state.toString();
    };
}
