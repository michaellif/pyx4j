/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 30, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial.offering;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.Concession;

public interface Feature extends IEntity {

    @Caption(name = "Offering Start")
    @MemberColumn(name = "featureStart")
    IPrimitive<LogicalDate> start();

    @Caption(name = "Offering End")
    @MemberColumn(name = "featureEnd")
    IPrimitive<LogicalDate> end();

    IPrimitive<String> description();

    IList<Concession> concessions();
}
