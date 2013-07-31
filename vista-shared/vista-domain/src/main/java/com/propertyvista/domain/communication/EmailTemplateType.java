/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.communication;

import java.util.EnumSet;
import java.util.Set;

import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

@I18n
public enum EmailTemplateType {

    PasswordRetrievalCrm,

    PasswordRetrievalProspect,

    PasswordRetrievalTenant,

    ApplicationCreatedApplicant,

    ApplicationCreatedCoApplicant,

    ApplicationCreatedGuarantor,

    ApplicationApproved,

    ApplicationDeclined,

    TenantInvitation,

    MaintenanceRequestCreatedPMC,

    MaintenanceRequestCreatedTenant,

    MaintenanceRequestUpdated,

    MaintenanceRequestCompleted,

    MaintenanceRequestCancelled,

    MaintenanceRequestEntryNotice;

    public static Set<EmailTemplateType> maintenanceTemplates() {
        return EnumSet.of(

        MaintenanceRequestCreatedPMC, MaintenanceRequestCreatedTenant, MaintenanceRequestUpdated,

        MaintenanceRequestCompleted, MaintenanceRequestCancelled, MaintenanceRequestEntryNotice);
    }

    @Override
    public String toString() {
        return I18nEnum.toString(this);
    }

}
