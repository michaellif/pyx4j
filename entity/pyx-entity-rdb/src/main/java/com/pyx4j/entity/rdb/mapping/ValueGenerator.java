/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Mar 17, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mapping;

import java.io.Serializable;
import java.util.UUID;

import com.pyx4j.entity.annotations.GeneratedValue;
import com.pyx4j.entity.core.meta.MemberMeta;

public class ValueGenerator {

    static Serializable generate(GeneratedValue generatedValue, MemberMeta memberMeta) {
        switch (generatedValue.type()) {
        case randomUUID:
            return UUID.randomUUID().toString();
        default:
            throw new IllegalArgumentException("Unsupported @GeneratedValue.type '" + generatedValue.type() + "' on member '" + memberMeta.getFieldName() + "'");
        }
    }
}
