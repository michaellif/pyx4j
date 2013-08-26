/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-17
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlTransient;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.tenant.lease.Tenant;

@Transient
@XmlTransient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface PadProcessorInformation extends IEntity {

    public static enum PadProcessingStatus {

        ignoredByRequest,

        invalid,

        anotherRecordInvalid,

        notFound,

        mergedWithAnotherRecord,

        ignoredUinitializedChargeSplit,

        notUsedForACH,

        invalidResultingValues,

        // Statuses set in persist

        unchangedInDB,
    }

    Tenant tenant();

    IPrimitive<PadProcessingStatus> status();

    IList<PadFileModel> accountCharges();

    public static final int PERCENT_SCALE = 6;

    IPrimitive<BigDecimal> percent();

    IPrimitive<BigDecimal> percentNotRounded();

    IPrimitive<BigDecimal> estimatedChargeSplit();

    // Maps to BillableItem.uid
    IPrimitive<String> billableItemId();

    IPrimitive<BigDecimal> chargeAmount();

    IPrimitive<BigDecimal> chargeEftAmount();

    IPrimitive<BigDecimal> accountEftAmountTotal();

    IPrimitive<BigDecimal> accountChargeTotal();

    IPrimitive<BigDecimal> calulatedEftTotalAmount();

    IPrimitive<BigDecimal> actualChargeCodeAmount();

}
