/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 8, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.yardi;

import com.propertyvista.yardi.stubs.YardiILSGuestCardStub;
import com.propertyvista.yardi.stubs.YardiMaintenanceRequestsStub;
import com.propertyvista.yardi.stubs.YardiResidentTransactionsStub;

public enum YardiInterfaceType {

    BillingAndPayments(YardiResidentTransactionsStub.class),

    Maintenance(YardiMaintenanceRequestsStub.class),

    ILSGuestCard(YardiILSGuestCardStub.class),

    CreditScreening(null),

    Collections(null);

    public final Class<? extends YardiInterface> ifClass;

    private YardiInterfaceType(Class<? extends YardiInterface> ifClass) {
        this.ifClass = ifClass;
    }
}