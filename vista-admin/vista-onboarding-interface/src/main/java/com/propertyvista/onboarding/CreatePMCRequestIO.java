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
 * Created on Mar 2, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.onboarding;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.i18n.annotations.I18n;

/**
 * Notifies and provides the detailed data about newly registered PMC.
 * 
 * Create user that can modify and activate this PMC
 */
@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface CreatePMCRequestIO extends RequestIO {

    /**
     * Company name
     */
    @NotNull
    IPrimitive<String> name();

    @NotNull
    IPrimitiveSet<String> dnsNameAliases();

    @NotNull
    IPrimitive<String> adminUserEmail();

    @NotNull
    IPrimitive<String> adminUserpassword();
}
