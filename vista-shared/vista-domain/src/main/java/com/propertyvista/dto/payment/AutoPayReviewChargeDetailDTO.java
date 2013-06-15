/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.dto.payment;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.tenant.lease.BillableItem;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface AutoPayReviewChargeDetailDTO extends IEntity {

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> totalPrice();

    //Special values for  "Removed item"  if  billableItem().isNull() and  "New"  if percentChange().isNull()
    @Caption(name = "% Change")
    @Format("#.##%")
    @Editor(type = EditorType.percentage)
    IPrimitive<BigDecimal> percentChange();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> payment();

    @Caption(name = "% of Total")
    @Format("#.##%")
    @Editor(type = EditorType.percentage)
    IPrimitive<BigDecimal> percent();

    // References to actual data (not used in report display)
    @Detached
    BillableItem billableItem();
}
