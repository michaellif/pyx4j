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
package com.propertyvista.operations.domain.security;

import java.util.Date;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.security.AuditRecordEventType;
import com.propertyvista.domain.security.common.VistaApplication;

@Table(namespace = VistaNamespace.operationsNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface AuditRecord extends IEntity {

    @Length(63)
    @Indexed(group = { "f,2" })
    IPrimitive<String> namespace();

    @Indexed(group = { "f,1" })
    @MemberColumn(name = "usr")
    @Caption(name = "Modified by")
    IPrimitive<Key> user();

    @Length(39)
    IPrimitive<String> remoteAddr();

    @Caption(name = "When")
    @Format("MM/dd/yyyy HH:mm")
    @Timestamp(Timestamp.Update.Created)
    IPrimitive<Date> created();

    @Format("MM/dd/yyyy HH:mm")
    IPrimitive<Date> worldTime();

    IPrimitive<AuditRecordEventType> event();

    IPrimitive<VistaApplication> app();

    IPrimitive<Key> entityId();

    IPrimitive<String> entityClass();

    @Length(1024)
    IPrimitive<String> details();
}
