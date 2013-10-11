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

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.payment.AutopayAgreement;

/**
 * @see AutopayAgreement.PreauthorizedPaymentCoveredItem
 */
@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface AutoPayReviewChargeDTO extends IEntity {

    IPrimitive<String> leaseCharge();

    AutoPayReviewChargeDetailDTO previous();

    AutoPayReviewChargeDetailDTO current();
}
