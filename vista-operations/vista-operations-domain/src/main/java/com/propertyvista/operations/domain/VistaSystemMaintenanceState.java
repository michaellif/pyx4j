/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-04
 * @author vlads
 */
package com.propertyvista.operations.domain;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.essentials.rpc.admin.SystemMaintenanceState;
import com.pyx4j.i18n.annotations.I18n;

/**
 * All boolean are set to false by default.
 */
@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface VistaSystemMaintenanceState extends SystemMaintenanceState {

    @Caption(description = "When checked disables TenantSure on tenant portal")
    IPrimitive<Boolean> enableTenantSureMaintenance();

    IPrimitive<Boolean> enableFundsTransferMaintenance();

    IPrimitive<Boolean> enableCreditCardMaintenance();

    IPrimitive<Boolean> enableCreditCardConvenienceFeeMaintenance();

    IPrimitive<Boolean> enableInteracMaintenance();

    IPrimitive<Boolean> enableEquifaxMaintenance();

    IPrimitive<Boolean> enableSFTPInterfaceMaintenance();

    //N.B. In PMC it can be enabled even when globally it is disabled.
    VistaApplicationsSystemMaintenanceState applications();

}
