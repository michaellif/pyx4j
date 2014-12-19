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
 * @author ArtyomB
 */
package com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.errors;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.i18n.shared.I18n;

public class TenantSureOnMaintenanceException extends UserRuntimeException {

    private static final long serialVersionUID = -3096426577244188121L;

    private static final I18n i18n = I18n.get(TenantSureOnMaintenanceException.class);

    public TenantSureOnMaintenanceException(String message) {
        super(message);
    }

    public TenantSureOnMaintenanceException() {
        super(i18n.tr("We are sorry but our internet connection to TenantSure is currently unavailable! Please try later..."));
    }

}
