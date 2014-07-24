/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 24, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.financial;

import java.util.Date;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.security.CustomerSignature;

@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface SignedWebPaymentTerm extends IEntity {

    // This is versioned reference  to operations  VistaTerms.TenantPaymentWebPaymentFeeTerms
    IPrimitive<Key> term();

    IPrimitive<Date> termFor();

    @Owned
    @Caption(name = "I agree to the service fee being charged and have read the applicable terms and conditions")
    CustomerSignature signature();

}
