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
 * Created on Feb 10, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.domain.crm;

import java.io.Serializable;
import java.util.Date;

import com.pyx4j.entity.adapters.index.KeywordsIndexAdapter;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface Resource extends IEntity {

    public enum RepStatus implements Serializable {

        ACTIVE,

        INACTIVE;

        RepStatus() {
        }

    }

    @NotNull
    @Indexed(keywordLength = 2, adapters = KeywordsIndexAdapter.class)
    @ToString
    IPrimitive<String> name();

    @NotNull
    IPrimitive<RepStatus> status();

    @Editor(type = EditorType.phone)
    IPrimitive<String> phone();

    @Caption(name = "Address")
    @Owned
    Address address();

    @Timestamp
    IPrimitive<Date> updated();
}
