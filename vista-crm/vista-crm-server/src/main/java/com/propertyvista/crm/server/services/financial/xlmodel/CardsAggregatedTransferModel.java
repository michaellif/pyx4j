/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 5, 2014
 * @author ernestog
 * @version $Id$
 */
package com.propertyvista.crm.server.services.financial.xlmodel;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

public interface CardsAggregatedTransferModel extends IEntity {

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> visaDeposit();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> visaFee();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    @Caption(name = "Master*C*ard Deposit")
    IPrimitive<BigDecimal> mastercardDeposit();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    @Caption(name = "Master*C*ard Fee")
    IPrimitive<BigDecimal> mastercardFee();

}
