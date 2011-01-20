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
 * Created on Feb 9, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.domain.crm;

import java.util.Date;

import com.pyx4j.entity.adapters.index.EnumCollectionIndexAdapter;
import com.pyx4j.entity.adapters.index.GeoPointIndexAdapter;
import com.pyx4j.entity.adapters.index.KeywordsIndexAdapter;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.rpc.GeoCriteria;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.essentials.rpc.report.ReportColumn;
import com.pyx4j.examples.domain.crm.Order.OrderStatus;
import com.pyx4j.geo.GeoPoint;

public interface Customer extends IEntity {

    @NotNull
    @Indexed(global = 'n', keywordLenght = 2, adapters = KeywordsIndexAdapter.class)
    @ToString
    IPrimitive<String> name();

    @Indexed(global = 'p', keywordLenght = 3, indexPrimaryValue = false, adapters = KeywordsIndexAdapter.class)
    @Editor(type = EditorType.phone)
    IPrimitive<String> phone();

    @Caption(name = "Address")
    @Owned
    @Indexed
    Address address();

    @Indexed(indexPrimaryValue = false, adapters = GeoPointIndexAdapter.class)
    IPrimitive<GeoPoint> location();

    @Transient
    @Caption(name = "From Location (Zip)")
    GeoCriteria locationCriteria();

    @ReportColumn(ignore = true)
    IPrimitive<String> panoId();

    @ReportColumn(ignore = true)
    IPrimitive<Double> panoYaw();

    @Editor(type = EditorType.textarea)
    IPrimitive<String> note();

    @Timestamp
    IPrimitive<Date> updated();

    //Search columns

    @RpcTransient
    @ReportColumn(ignore = true)
    @Detached
    ISet<Order> orders();

    @ReportColumn(ignore = true)
    @Caption(name = "Order Status")
    @Indexed(global = 'o', indexPrimaryValue = false, adapters = EnumCollectionIndexAdapter.class)
    IPrimitiveSet<OrderStatus> orderStatus();

}
