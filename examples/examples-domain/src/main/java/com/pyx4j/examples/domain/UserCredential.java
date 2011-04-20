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
 * Created on Feb 16, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.domain;

import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.RpcBlacklist;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@RpcBlacklist
@Table(primaryKeyStrategy = Table.PrimaryKeyStrategy.ASSIGNED)
public interface UserCredential extends IEntity {

    IPrimitive<Boolean> enabled();

    @RpcTransient
    IPrimitive<String> credential();

    @RpcTransient
    IPrimitive<String> activationKey();

    @RpcTransient
    IPrimitive<String> accessKey();

    @Indexed
    @MemberColumn(name = "usr")
    User user();

    IPrimitive<ExamplesBehavior> behavior();
}
