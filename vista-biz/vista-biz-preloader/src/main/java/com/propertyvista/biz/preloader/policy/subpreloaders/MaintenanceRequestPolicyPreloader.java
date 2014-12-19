/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 31, 2014
 * @author stanp
 */
package com.propertyvista.biz.preloader.policy.subpreloaders;

import java.sql.Time;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.preloader.policy.AbstractPolicyPreloader;
import com.propertyvista.config.VistaLocale;
import com.propertyvista.domain.maintenance.EntryInstructionsNote;
import com.propertyvista.domain.maintenance.EntryNotGrantedAlert;
import com.propertyvista.domain.maintenance.MaintenanceRequestWindow;
import com.propertyvista.domain.maintenance.PermissionToEnterNote;
import com.propertyvista.domain.policy.policies.MaintenanceRequestPolicy;
import com.propertyvista.shared.i18n.CompiledLocale;

public class MaintenanceRequestPolicyPreloader extends AbstractPolicyPreloader<MaintenanceRequestPolicy> {

    public MaintenanceRequestPolicyPreloader() {
        super(MaintenanceRequestPolicy.class);
    }

    @Override
    protected MaintenanceRequestPolicy createPolicy(StringBuilder log) {
        MaintenanceRequestPolicy policy = EntityFactory.create(MaintenanceRequestPolicy.class);
        // get English locale
        CompiledLocale eng = VistaLocale.toCompiledLocale(VistaLocale.getPmcDefaultEnglishLocale());
        if (eng != null) {
            PermissionToEnterNote permissionNote = policy.permissionToEnterNote().$();
            permissionNote.locale().setValue(eng);
            permissionNote.caption().setValue("Permission To Enter");
            permissionNote.text().setValue(//
                    "By checking this box you authorize the Landlord to enter your Apartment to assess and resolve the issue." //
            );
            policy.permissionToEnterNote().add(permissionNote);

            EntryNotGrantedAlert noEntryAlert = policy.entryNotGrantedAlert().$();
            noEntryAlert.locale().setValue(eng);
            noEntryAlert.title().setValue("Confirm 'No Entry'");
            noEntryAlert.text().setValue(//
                    "Please confirm that you do not wish to grant our staff Permission To Enter your apartment. " + //
                            "Please note that this may delay resolution of reported issue." //
            );
            policy.entryNotGrantedAlert().add(noEntryAlert);

            EntryInstructionsNote entryNote = policy.entryInstructionsNote().$();
            entryNote.locale().setValue(eng);
            entryNote.caption().setValue("Entry Instructions");
            entryNote.text().setValue("Special instructions when entering your apartment (e.g. pet alert, etc)");
            policy.entryInstructionsNote().add(entryNote);
        }

        // add default time windows to select by tenant as preferred time
        MaintenanceRequestWindow window = policy.tenantPreferredWindows().$();
        window.timeFrom().setValue(Time.valueOf("08:00:00"));
        window.timeTo().setValue(Time.valueOf("12:00:00"));
        policy.tenantPreferredWindows().add(window);
        window = policy.tenantPreferredWindows().$();
        window.timeFrom().setValue(Time.valueOf("13:00:00"));
        window.timeTo().setValue(Time.valueOf("17:00:00"));
        policy.tenantPreferredWindows().add(window);
        // set scheduling options
        policy.allow24HourSchedule().setValue(false);
        policy.schedulingWindow().timeFrom().setValue(Time.valueOf("08:00:00"));
        policy.schedulingWindow().timeTo().setValue(Time.valueOf("18:00:00"));
        policy.maxAllowedWindowHours().setValue(4);
        policy.minAdvanceNoticeHours().setValue(24);

        return policy;
    }

}
