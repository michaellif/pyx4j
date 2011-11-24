/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 24, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.communication;

import java.util.Date;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface Message extends IEntity {

    enum MessageType {
        communication, maintananceAlert, paymnetPastDue, paymentMethodExpired
    }

    IPrimitive<String> subject();

    IPrimitive<String> text();

    IPrimitive<Date> date();

    IPrimitive<MessageType> type();

    IPrimitive<Boolean> acknowledged();

}
