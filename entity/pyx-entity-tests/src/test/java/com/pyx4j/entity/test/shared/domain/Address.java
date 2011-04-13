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
 * Created on Dec 23, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared.domain;

import java.util.Date;

import com.pyx4j.entity.annotations.Reference;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.test.shared.adapters.ProvinceReferenceAdapter;

@Table(prefix = "test")
public interface Address extends IEntity {

    public static int TEST_DECLARED_MEMBERS = 6;

    @ToString(index = 3)
    Country country();

    @Reference(adapter = ProvinceReferenceAdapter.class)
    Province province();

    @ToString(index = 2)
    City city();

    @ToString(index = 1)
    IPrimitive<String> streetName();

    IPrimitive<java.sql.Date> effectiveFrom();

    IPrimitive<Date> effectiveTo();

    //TODO
    //IPrimitive<GeoPoint> location();
}
