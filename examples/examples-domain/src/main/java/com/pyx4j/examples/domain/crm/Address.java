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
 * Created on Mar 4, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.domain.crm;

import com.pyx4j.entity.adapters.index.KeywordsIndexAdapter;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@EmbeddedEntity
@ToStringFormat("{0}, {1}, {2} {3}")
public interface Address extends IEntity {

    @ToString(index = 0)
    @Indexed(global = 's', keywordLength = 2, adapters = KeywordsIndexAdapter.class)
    IPrimitive<String> street();

    @ToString(index = 1)
    @Indexed(global = 'c', keywordLength = 2, adapters = KeywordsIndexAdapter.class)
    IPrimitive<String> city();

    @ToString(index = 2)
    IPrimitive<Province> province();

    @Caption(name = "Zip/Postal")
    @ToString(index = 3)
    IPrimitive<String> zip();
}
