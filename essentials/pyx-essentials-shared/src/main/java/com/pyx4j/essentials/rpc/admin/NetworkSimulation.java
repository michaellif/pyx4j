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
 * Created on 2010-09-15
 * @author vlads
 */
package com.pyx4j.essentials.rpc.admin;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface NetworkSimulation extends IEntity {

    // Available by system configuration properties
    IPrimitive<Boolean> available();

    IPrimitive<Boolean> enabled();

    @Caption(name = "Delay", description = "milliseconds")
    IPrimitive<Integer> delay();

    @Caption(name = "URI Pattern", description = "regular expression: .*/Service.function")
    IPrimitive<String> httpRequestURIPattern();

    @Caption(name = "Start Number")
    IPrimitive<Integer> httpRequestStartNumber();

    @Caption(name = "Request Count")
    IPrimitive<Integer> httpRequestCount();

    IPrimitive<Integer> httpResponseCode();

}
