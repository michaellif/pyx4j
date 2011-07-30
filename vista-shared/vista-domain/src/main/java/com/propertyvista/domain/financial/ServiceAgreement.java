/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 1, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial;

import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.financial.offeringnew.ChargeItem;
import com.propertyvista.domain.financial.offeringnew.Concession;

public interface ServiceAgreement extends IEntity {

    IList<ChargeItem> chargeItems();

    IList<Concession> concessions();

    @MemberColumn(name = "leaseAccount")
    Account account();

}
