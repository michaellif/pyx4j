/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-15
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.settings;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc;

@Table(prefix = "admin", namespace = VistaNamespace.operationsNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface PmcYardiCredential extends IEntity {

    @ReadOnly
    @Owner
    @MemberColumn(notNull = true)
    @JoinColumn
    @Indexed(uniqueConstraint = true)
    @Detached
    Pmc pmc();

    public enum Platform {
        SQL, Oracle
    }

    @Caption(name = "Resident Transactions Service URL")
    IPrimitive<String> residentTransactionsServiceURL();

    @Caption(name = "Sys Batch Service URL")
    IPrimitive<String> sysBatchServiceURL();

    @Caption(name = "Web Service User")
    IPrimitive<String> username();

    @Caption(name = "Password")
    IPrimitive<String> credential();

    IPrimitive<String> serverName();

    @MemberColumn(name = "db")
    IPrimitive<String> database();

    IPrimitive<Platform> platform();

    IPrimitive<String> propertyCode();

}
