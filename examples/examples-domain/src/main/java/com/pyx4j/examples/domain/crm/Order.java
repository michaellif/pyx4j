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

import com.pyx4j.entity.adapters.index.EnumIndexAdapter;
import com.pyx4j.entity.adapters.index.KeywordsIndexAdapter;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.essentials.rpc.report.ReportColumn;

public interface Order extends IEntity {

    public enum OrderStatus {

        ESTIMATE("Estimate"),

        ACTIVE("Active"),

        COMPLETED("Completed"),

        SUSPENDED("Suspended");

        private final String descr;

        private OrderStatus(String descr) {
            this.descr = descr;
        }

        @Override
        public String toString() {
            return descr;
        }
    }

    @ToString
    @Indexed(global = '#', keywordLenght = 3, adapters = KeywordsIndexAdapter.class)
    @Caption(name = "Order #")
    IPrimitive<Integer> orderNumber();

    @Format("MMM d, yyyy")
    IPrimitive<java.sql.Date> receivedDate();

    @Format("#.00")
    IPrimitive<Double> cost();

    @Owner
    //@Detached
    @ReportColumn(ignore = true)
    @Indexed
    Customer customer();

    /**
     * Copy of data from customer
     */
    @Indexed(global = 'n', keywordLenght = 2, indexPrimaryValue = false, adapters = KeywordsIndexAdapter.class)
    IPrimitive<String> customerName();

    /**
     * Copy of data from customer
     */
    @Indexed(global = 'p', keywordLenght = 3, indexPrimaryValue = false, adapters = KeywordsIndexAdapter.class)
    @Editor(type = EditorType.phone)
    IPrimitive<String> customerPhone();

    @Indexed
    Resource resource();

    @ToString
    @Indexed(global = 'd', keywordLenght = 3, indexPrimaryValue = false, adapters = KeywordsIndexAdapter.class)
    IPrimitive<String> description();

    IPrimitive<java.sql.Date> completedDate();

    IPrimitive<java.sql.Date> dueDate();

    @NotNull
    @Indexed(global = 'o', adapters = EnumIndexAdapter.class)
    IPrimitive<OrderStatus> status();

    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> notes();

    @Timestamp
    IPrimitive<Date> updated();
}
