/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-16
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.tenant;

import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.pmc.payment.CustomerCreditCheckTransaction;
import com.propertyvista.domain.tenant.CustomerCreditCheck;

@Transient
@ExtendsBO(CustomerCreditCheck.class)
public interface CustomerCreditCheckDTO extends CustomerCreditCheck {

    CustomerCreditCheckTransaction transaction();

    IPrimitive<String> transactionRef();

}
