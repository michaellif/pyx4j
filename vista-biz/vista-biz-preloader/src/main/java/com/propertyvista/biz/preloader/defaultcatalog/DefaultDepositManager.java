/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 17, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.biz.preloader.defaultcatalog;

import java.math.BigDecimal;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Product;
import com.propertyvista.domain.financial.offering.ProductDeposit.ValueType;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;

public class DefaultDepositManager {

    static void fillDefaultDeposits(Product<?> entity) {
        entity.version().depositLMR().enabled().setValue(false);
        entity.version().depositLMR().depositType().setValue(DepositType.LastMonthDeposit);
        entity.version().depositLMR().chargeCode().set(getARCode(ARCode.Type.Deposit));
        entity.version().depositLMR().valueType().setValue(ValueType.Percentage);
        entity.version().depositLMR().value().setValue(BigDecimal.ONE);
        entity.version().depositLMR().description().setValue(DepositType.LastMonthDeposit.toString());

        entity.version().depositMoveIn().enabled().setValue(false);
        entity.version().depositMoveIn().depositType().setValue(DepositType.MoveInDeposit);
        entity.version().depositMoveIn().chargeCode().set(getARCode(ARCode.Type.Deposit));
        entity.version().depositMoveIn().valueType().setValue(ValueType.Percentage);
        entity.version().depositMoveIn().value().setValue(BigDecimal.ONE);
        entity.version().depositMoveIn().description().setValue(DepositType.MoveInDeposit.toString());

        entity.version().depositSecurity().enabled().setValue(false);
        entity.version().depositSecurity().depositType().setValue(DepositType.SecurityDeposit);
        entity.version().depositSecurity().chargeCode().set(getARCode(ARCode.Type.Deposit));
        entity.version().depositSecurity().valueType().setValue(ValueType.Percentage);
        entity.version().depositSecurity().value().setValue(BigDecimal.ONE);
        entity.version().depositSecurity().description().setValue(DepositType.SecurityDeposit.toString());
    }

    static ARCode getARCode(ARCode.Type type) {
        EntityQueryCriteria<ARCode> criteria = EntityQueryCriteria.create(ARCode.class);
        criteria.eq(criteria.proto().type(), type);
        return Persistence.service().retrieve(criteria);
    }
}
