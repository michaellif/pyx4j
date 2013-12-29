/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 16, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.pmc;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.VistaNamespace;

@Table(prefix = "admin", namespace = VistaNamespace.operationsNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface PmcDnsName extends IEntity {

    @I18n
    public enum DnsNameTarget {

        crm,

        field,

        site,

        portal;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @ReadOnly
    @Indexed
    @Owner
    @MemberColumn(notNull = true)
    @JoinColumn
    Pmc pmc();

    @OrderColumn
    IPrimitive<Integer> odr();

    @Length(253)
    @Indexed(uniqueConstraint = true, ignoreCase = true)
    @NotNull
    IPrimitive<String> dnsName();

    @Caption(name = "Active")
    IPrimitive<Boolean> enabled();

    @NotNull
    IPrimitive<DnsNameTarget> target();

    IPrimitive<Boolean> httpsEnabled();

    @Length(150)
    IPrimitive<String> googleAPIKey();

    @Length(15)
    IPrimitive<String> googleAnalyticsId();
}
