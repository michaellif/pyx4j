/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-01
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.domain.tenantsure;

import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@Table(namespace = VistaNamespace.adminNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface TenantSureHQUpdateRecord extends IEntity {

    @Owner
    @JoinColumn
    @Indexed
    @MemberColumn(notNull = true)
    TenantSureHQUpdateFile file();

    public enum RequestType {

        Cancell,

    };

    @NotNull
    IPrimitive<String> certificateNumber();

    IPrimitive<Boolean> consumed();
}
