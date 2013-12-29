/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.pmc;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@AbstractEntity
public interface EquifaxLimit extends IEntity {

    IPrimitive<Integer> dailyRequests();

    IPrimitive<Integer> dailyReports();
}
