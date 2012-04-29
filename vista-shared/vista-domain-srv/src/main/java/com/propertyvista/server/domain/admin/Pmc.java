/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.domain.admin;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.PmcDnsName;
import com.propertyvista.domain.VistaNamespace;

@Table(prefix = "admin", namespace = VistaNamespace.adminNamespace)
@Caption(name = "PMC")
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface Pmc extends IEntity {

    public static final String adminNamespace = VistaNamespace.adminNamespace;

    @NotNull
    @ToString
    IPrimitive<String> name();

    @NotNull
    @ReadOnly
    @Length(63)
    @Indexed(uniqueConstraint = true)
    //TODO Temporary until DB migration is finished
    @Transient
    IPrimitive<String> namespace();

    @NotNull
    @Length(63)
    @Indexed(uniqueConstraint = true)
    IPrimitive<String> dnsName();

    @Owned
    IList<PmcDnsName> dnsNameAliases();

    //TODO Temporary until DB migration is finished
    @Transient
    IPrimitive<String> onboardingAccountId();

    @Timestamp(Timestamp.Update.Created)
    @ReadOnly
    IPrimitive<Date> created();

}
