/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 21, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.account;

import java.util.Date;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.security.AuditRecordEventType;

@Transient
public interface LoginAttemptDTO extends IEntity {

    @Caption(name = "When")
    @Format("MM/dd/yyyy HH:mm")
    IPrimitive<Date> time();

    @Length(16)
    IPrimitive<String> remoteAddress();

    IPrimitive<AuditRecordEventType> outcome();

    /** This is not for display, just a hack to bind criteria: never supposed to contain any data */
    IPrimitive<Key> userKey();
}
