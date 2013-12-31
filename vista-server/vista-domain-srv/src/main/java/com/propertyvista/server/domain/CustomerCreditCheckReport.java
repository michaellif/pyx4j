/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.domain;

import java.util.Date;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

/**
 * The data in this table will not bee stored in backup for 52 days (60 - 8 days).
 */
@RpcTransient
@Table(namespace = VistaNamespace.expiringNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface CustomerCreditCheckReport extends IEntity {

    @ReadOnly
    @MemberColumn(notNull = true)
    IPrimitive<Key> pmc();

    @MemberColumn(notNull = true)
    IPrimitive<Key> publicKey();

    @Timestamp(Timestamp.Update.Created)
    @ReadOnly
    @MemberColumn(notNull = true)
    IPrimitive<Date> created();

    @ReadOnly
    @MemberColumn(notNull = true)
    IPrimitive<Key> customer();

    @RpcTransient
    @ReadOnly
    @Length(15 * 1024 * 1024)
    IPrimitive<byte[]> data();

}
