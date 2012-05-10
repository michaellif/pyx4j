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
 * Created on 2012-05-10
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.domain.pmc;

import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@Table(prefix = "admin", namespace = VistaNamespace.adminNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface PmcAccountNumbers extends IEntity {

    public enum AccountNumbersRangeType {
        // From:  800-ppp-ppp-ttt-tc    to: 999-ppp-ppp-ppp-tc
        Small,

        // From:  500-ppp-ctt-ttt-tc    to: 799-ppp-ppp-tttt-tc
        Medium,

        // From:  000-ppp-ttt-ttt-tc    to: 499-ppp-ttt-ttt-tc
        Large
    }

    @NotNull
    @Indexed(uniqueConstraint = true, group = { "m,1" })
    IPrimitive<Long> accountPrefix();

    @NotNull
    @Indexed(uniqueConstraint = true, group = { "m,2" })
    IPrimitive<AccountNumbersRangeType> pmcType();

    Pmc pmc();

}
