/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 24, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.misc;

import com.pyx4j.config.shared.ApplicationMode;

public class VistaTODO {

    /** enables wizards in CRM that set up credit check (equifax) and online payments */
    public static final boolean ENABLE_ONBOARDING_WIZARDS = ApplicationMode.isDevelopment();

    /*
     * This is changed by BuildMaster when creating Branch.
     * Used by configurations to enable dual development environments.
     */
    public static final boolean codeBaseIsProdBranch = false;

    /**
     * TODO: Do not forget to turn it on/off during/after production release !!!
     */
    public static boolean removedForProduction = true;

    /**
     * TODO: Do not forget to turn it on/off during/after production release !!!
     */
    public static final boolean ENABLE_COMMUNCATION_CENTER = false;

    public static final boolean tenantSureDisabledForProduction = true;

    /**
     * Set <code>true</code> to let PtApp show a demo wizard for approved tenants
     */
    public static final boolean enableWelcomeWizardDemoMode = true;

    /**
     * Check that lease from is in the future compared to transaction time (currently if is true billing tests fail)
     */
    public static final boolean checkLeaseDatesOnUnitReservation = false;

    public static final boolean isAfterBeta04Version = false;

    public static boolean vladsLeaseMigration = true;

    /** joins with conditions */
    public static final boolean complextQueryCriteria = true;

    public static final boolean VISTA_1588 = true;

    public static final boolean Equifax_Short_VISTA_478 = false;

    public static final boolean Equifax_Long_VISTA_478 = true;

    public static final boolean UploadedBlobSecurity = true;

    public static final boolean ApplicationDocumentationPolicyRefacotring = true;

    public static final boolean VISTA_2127_Attachments_For_Notes = true;

    public static final boolean VISTA_2108_View_Lease_Application = false;

    public static final boolean VISTA_1789_Renew_Lease = false;

    public static final boolean VISTA_1756_Concessions_Should_Be_Hidden = true;

    public static final boolean VISTA_2242_Simple_Lease_Renewal = true;

    public static boolean VISTA_2256_Default_Product_Catalog_Show = false;

    public static boolean VISTA_2446_Periodic_Lease_Terms = false;

    public static boolean VISTA_3129_AutoPayBulkEditorExperiment = true;
}
