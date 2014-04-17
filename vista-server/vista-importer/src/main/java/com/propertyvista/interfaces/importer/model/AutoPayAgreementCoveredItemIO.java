/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.model;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@ToStringFormat("{0,choice,null#|!null#{0}}, amount: ${1}, charge: ${2}")
public interface AutoPayAgreementCoveredItemIO extends IEntity {

    @ToString(index = 0)
    IPrimitive<String> chargeCode();

    IPrimitive<String> chargeId();

    IPrimitive<String> chargeARCodeType();

    IPrimitive<String> description();

    @ToString(index = 1)
    IPrimitive<BigDecimal> amount();

    @ToString(index = 2)
    IPrimitive<BigDecimal> chargeAmount();
}
