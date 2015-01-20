/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 24, 2014
 * @author smolka
 */
package com.propertyvista.domain.communication;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.SecurityEnabled;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.security.CrmRole;

@ToStringFormat("{0}")
@Table(prefix = "communication")
@SecurityEnabled
public interface MessageCategory extends IEntity {

    @I18n(context = "MessageGroup")
    @XmlType(name = "Type")
    public enum CategoryType {

        Message, Ticket;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n(context = "TicketType")
    @XmlType(name = "TicketType")
    public enum TicketType {
        @Translate("Tenant")
        Tenant,

        @Translate("Landlord")
        Landlord,

        @Translate("Vendor")
        Vendor,

        @Translate("Maintenance")
        Maintenance;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    @ToString(index = 0)
    IPrimitive<String> category();

    @NotNull
    IPrimitive<CategoryType> categoryType();

    @NotNull
    IPrimitive<TicketType> ticketType();

    @NotNull
    IPrimitive<Boolean> deleted();

    @NotNull
    @Detached
    @MemberColumn(name = "dispatchers")
    IList<Employee> dispatchers();

    @NotNull
    @Detached
    @MemberColumn(name = "rls")
    IList<CrmRole> roles();
}
