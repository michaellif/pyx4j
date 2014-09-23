/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 22, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.mock.model.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

public interface YardiGuestEvent extends IEntity {

    public enum Type {
        APPLICATION, APPOINTMENT, APPROVE, CANCEL, CANCEL_APPLICATION, CONTACT, LEASE_SIGN, HOLD, RELEASE, OTHER
    }

    IPrimitive<String> eventId();

    IPrimitive<Type> type();

    IPrimitive<Date> date();

    IPrimitive<String> agentName();

    IPrimitive<BigDecimal> rentQuote();
}
