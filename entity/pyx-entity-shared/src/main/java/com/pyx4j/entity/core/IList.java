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
 * Created on Jan 24, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.core;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IList<TYPE extends IEntity> extends ICollection<TYPE, List<Map<String, Serializable>>>, List<TYPE> {

    public void set(IList<TYPE> typedList);

    /**
     * Move element to new position in list
     */
    void move(int originalIndex, int targetIndex);

    /**
     * Returns the element contained in this list.
     * 
     * @param element
     * @return null if element no found
     */
    public TYPE get(TYPE element);

    /**
     * Create new instance Prototype of the Value object with special handling for item
     * position in a list.
     */
    //public TYPE $(int idx);

}
