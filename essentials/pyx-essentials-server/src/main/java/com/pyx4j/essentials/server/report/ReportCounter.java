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
 * Created on 2010-05-18
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.report;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class ReportCounter<E extends Serializable> implements Serializable {

    private static final long serialVersionUID = -328153445017511099L;

    protected HashMap<E, Integer> itemsCount = new HashMap<E, Integer>();

    public ReportCounter() {

    }

    public void add(E item, int count) {
        Integer counter = itemsCount.get(item);
        if (counter == null) {
            counter = new Integer(count);
        } else {
            counter = new Integer(counter + count);
        }
        itemsCount.put(item, counter);
    }

    public Integer getValue(E item) {
        return itemsCount.get(item);
    }

    public Set<Entry<E, Integer>> entrySet() {
        return itemsCount.entrySet();
    }

    public int size() {
        return itemsCount.size();
    }
}
