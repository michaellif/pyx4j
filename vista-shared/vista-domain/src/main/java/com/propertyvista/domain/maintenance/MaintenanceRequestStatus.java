/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 26, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.maintenance;

import java.util.EnumSet;
import java.util.Set;

import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

@I18n(context = "Maintenance Request")
public enum MaintenanceRequestStatus {

    Submitted, Scheduled, Resolved, Cancelled;

    public static Set<MaintenanceRequestStatus> opened() {
        return EnumSet.of(Submitted, Scheduled);
    }

    public static Set<MaintenanceRequestStatus> closed() {
        return EnumSet.of(Resolved, Cancelled);
    }

    @Override
    public String toString() {
        return I18nEnum.toString(this);
    }
}