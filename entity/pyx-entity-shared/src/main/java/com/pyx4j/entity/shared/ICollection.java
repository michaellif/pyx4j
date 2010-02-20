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
 * Created on Feb 20, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared;

import java.util.Collection;

/**
 * This represents AbstractCollection. Do not use this type for IEntity declarations, Use
 * concrete types ISet or IList.
 */
public interface ICollection<TYPE extends IEntity, VALUE_TYPE> extends IObject<VALUE_TYPE>, Collection<TYPE> {

    public Class<TYPE> getValueClass();

    /**
     * Create new instance of the Value object
     */
    public TYPE $();

}
