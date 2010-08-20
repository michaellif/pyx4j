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
 * Created on 2010-08-20
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server.search;

import java.util.Date;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;

public class DayRangeInMemoryFilter extends InMemoryFilter {

    final Date from;

    final Date to;

    public DayRangeInMemoryFilter(Path propertyPath, Date from, Date to) {
        super(propertyPath);
        this.from = from;
        this.to = to;
    }

    @Override
    protected boolean accept(IEntity entity) {
        Date value = (Date) entity.getValue(propertyPath);
        if (value == null) {
            return false;
        } else if ((to != null) && (value.after(to))) {
            return false;
        } else if ((from != null) && (value.before(from))) {
            return false;
        } else {
            return true;
        }
    }

}
