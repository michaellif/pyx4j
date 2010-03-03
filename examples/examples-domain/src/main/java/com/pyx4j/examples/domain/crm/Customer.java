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
 * Created on Feb 9, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.domain.crm;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.examples.domain.User;
import com.pyx4j.geo.GeoPoint;

public interface Customer extends IEntity {

    @NotNull
    @Indexed(keywordLenght = 2)
    IPrimitive<String> name();

    @Indexed(keywordLenght = 2)
    // TODO Use IPrimitiveSet<String>
    IPrimitive<String> phone();

    @Caption(name = "Address")
    @Indexed(keywordLenght = 2)
    IPrimitive<String> street();

    @Caption(name = "Zip/Postal")
    IPrimitive<String> zip();

    @Indexed
    IPrimitive<GeoPoint> location();

    IPrimitive<String> panoId();

    IPrimitive<Double> panoYaw();

    @Timestamp
    IPrimitive<Date> updated();

    @Owned
    ISet<Order> orders();

    // TODO Use IPrimitiveSet<String>
    @Editor(type = EditorType.textarea)
    IPrimitive<String> note();

    //IPrimitiveSet<String> notes();

    @Detached
    User user();
}
