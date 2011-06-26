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
 * Created on 2011-06-26
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.paypad.domain;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface RequestLog extends IEntity {

    IPrimitive<String> messageID();

    IPrimitive<String> merchantId();

    IPrimitive<String> requestID();

    IPrimitive<String> transactionType();

    IPrimitive<Float> amount();

    IPrimitive<String> reference();

    IPrimitive<String> responseCode();

}
