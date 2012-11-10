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
 * Created on Feb 1, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public interface IPrimitiveSet<TYPE extends Serializable> extends IObject<Set<TYPE>>, Set<TYPE> {

    @Override
    public Class<TYPE> getValueClass();

    public void set(IPrimitiveSet<TYPE> typedSet);

    public void setArrayValue(TYPE[] value) throws ClassCastException;

    public void setCollectionValue(Collection<TYPE> value) throws ClassCastException;

    public boolean containsAny(TYPE... value);
}
