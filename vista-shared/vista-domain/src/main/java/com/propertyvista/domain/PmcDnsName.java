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
package com.propertyvista.domain;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

@Table(prefix = "admin")
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface PmcDnsName extends IEntity {

    @I18n
    public enum DnsNameTarget {

        vistaCrm,

        residentPortal,

        prospectPortal;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Length(253)
    @Indexed(uniqueConstraint = true)
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
