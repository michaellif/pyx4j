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
 * Created on Sep 5, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.site.shared.domain.cusomization;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@ToStringFormat("{0}")
@AbstractEntity
public interface CustomizationHolder extends IEntity {

    @Length(100)
    IPrimitive<String> baseClass();

    @Length(255)
    @Indexed(group = "a,1")
    IPrimitive<String> className();

    @ToString(index = 0)
    @Length(255)
    @Indexed(group = "a,2")
    IPrimitive<String> identifierKey();

    @Length(20845)
    IPrimitive<String> serializedForm();

}
