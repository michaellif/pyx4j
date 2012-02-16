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
 * Created on Feb 15, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared;

import java.util.Date;

import com.pyx4j.entity.annotations.ManagedColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Transient;

@Transient
public interface IVersionInfo extends IEntity {

    @ManagedColumn
    IPrimitive<Date> fromDate();

    @ManagedColumn
    IPrimitive<Date> toDate();

    @ManagedColumn
    @OrderColumn
    IPrimitive<Integer> versionNumber();

}
