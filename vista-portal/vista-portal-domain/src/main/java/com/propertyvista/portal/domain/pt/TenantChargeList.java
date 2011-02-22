/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 20, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.domain.pt;

import com.propertyvista.portal.domain.Money;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;

public interface TenantChargeList extends IEntity {

    IList<TenantCharge> charges();

    Money total();
}
