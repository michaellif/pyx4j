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
 * Created on Feb 4, 2016
 * @author vlads
 */
package com.pyx4j.essentials.rpc.admin;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface NetworkServiceSimulation extends IEntity {

    IPrimitive<Boolean> enabled();

    @Caption(name = "Delay", description = "milliseconds")
    IPrimitive<Integer> delay();

    @Transient
    @I18n(strategy = I18n.I18nStrategy.IgnoreAll)
    public interface InterfaceClassNamePattern extends IEntity {

        IPrimitive<String> classNamePattern();

        IPrimitive<String> methodNamePattern();
    }

    @Owned
    IList<InterfaceClassNamePattern> interfacePatterns();

//    IPrimitive<Integer> requestStartNumber();
//
//    IPrimitive<Integer> requestCount();

}
