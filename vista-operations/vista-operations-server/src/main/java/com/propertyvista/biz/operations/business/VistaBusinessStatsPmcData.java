/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-10
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.operations.business;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface VistaBusinessStatsPmcData extends IEntity {

    @Caption(name = "PMC Name")
    IPrimitive<String> name();

    IPrimitive<Boolean> active();

    IPrimitive<Date> lastLogin();

    IPrimitive<Integer> buildingCount();

    IPrimitive<Integer> newBuildingCount();

    IPrimitive<Integer> unitsCount();

    IPrimitive<Integer> tenantsCount();

    IPrimitive<Integer> newTenantsCount();

    IPrimitive<Integer> registeredTenantsCount();

    IPrimitive<Integer> tenantInsurance();

    IPrimitive<Integer> processedPayments();

    IPrimitive<Integer> newProcessedPayments();
}
