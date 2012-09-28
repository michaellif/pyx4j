/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2012-09-28
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared.domain.ownership.polymorphic.st2;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.shared.IPrimitive;

@DiscriminatorValue(value = "BidirectionalOneToMany2PlmChildAC")
public interface BidirectionalOneToMany2PlmSTChildAC extends BidirectionalOneToMany2PlmSTChildA {

    IPrimitive<String> valueAC();

}
