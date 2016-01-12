/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Jan 12, 2016
 * @author vlads
 */
package com.pyx4j.tester.server.crud.oneToOne;

import com.pyx4j.commons.Filter;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.test.shared.domain.join.OneToOneReadOwner;
import com.pyx4j.tester.shared.crud.oneToOne.OneToOneCrudWithInMemoryFilterService;

public class OneToOneCrudWithInMemoryFilterServiceImpl extends AbstractCrudServiceImpl<OneToOneReadOwner> implements OneToOneCrudWithInMemoryFilterService {

    public OneToOneCrudWithInMemoryFilterServiceImpl() {
        super(OneToOneReadOwner.class);
    }

    @Override
    protected Filter<OneToOneReadOwner> boFilter(EntityQueryCriteria<OneToOneReadOwner> criteria) {
        return new Filter<OneToOneReadOwner>() {

            @Override
            public boolean accept(OneToOneReadOwner input) {
                if (Integer.valueOf(input.name().getValue()) % 2 == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        };
    }
}
