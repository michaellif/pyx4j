/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.pmc.info;

import java.util.Date;

import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

public interface PmcSignature extends IEntity {

    @Format("MM/dd/yyyy hh:mm a")
    @MemberColumn(name = "signature_timestamp")
    IPrimitive<Date> timestamp();

    IPrimitive<String> ipAddress();

    IPrimitive<String> fullName();

}
