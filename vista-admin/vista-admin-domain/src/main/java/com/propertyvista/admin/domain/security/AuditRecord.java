/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-19
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.domain.security;

import java.util.Date;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@Table(namespace = VistaNamespace.adminNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface AuditRecord extends IEntity {

    public static enum EventType {

        Login,

        LoginFailed,

        Create,

        Read,

        Update,

        Info

    }

    public enum VistaApplication {

        admin,

        crm,

        resident,

        prospect;

    }

    @Length(63)
    @Indexed(group = { "f,2" })
    IPrimitive<String> namespace();

    @Indexed(group = { "f,1" })
    @MemberColumn(name = "usr")
    @Caption(name = "Modified by")
    IPrimitive<Key> user();

    @Length(16)
    IPrimitive<String> remoteAddr();

    @Caption(name = "When")
    @Timestamp(Timestamp.Update.Created)
    IPrimitive<Date> created();

    IPrimitive<EventType> event();

    IPrimitive<VistaApplication> app();

    IPrimitive<Key> entityId();

    IPrimitive<String> entityClass();

    IPrimitive<String> details();
}
