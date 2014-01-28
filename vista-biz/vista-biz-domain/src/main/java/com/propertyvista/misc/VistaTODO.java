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

public class VistaTODO {

    /** enables wizards in CRM that set up credit check (equifax) and online payments */
    public static final boolean ENABLE_ONBOARDING_WIZARDS_IN_DEVELOPMENT = true;

    /*
     * This is changed by BuildMaster when creating Branch.
     * Used by configurations to enable dual development environments.
     */
    public static final boolean codeBaseIsProdBranch = false;

    /**
     * TODO: Do not forget to turn it on/off during/after production release !!!
     */
    public static boolean removedForProduction = true;

    public static boolean removedForProductionOAPI = true;

    /**
     * TODO: Do not forget to turn it on/off during/after production release !!!
     */
    public static final boolean ENABLE_COMMUNCATION_CENTER = false;

    /**
     * Check that lease from is in the future compared to transaction time (currently if is true billing tests fail)
     */
    public static final boolean checkLeaseDatesOnUnitReservation = false;

    public static final boolean isAfterBeta04Version = false;

    public static boolean vladsLeaseMigration = true;

    public static final boolean VISTA_1588 = true;

    public static final boolean Equifax_Off_VISTA_478 = false;

    public static final boolean VISTA_1789_Renew_Lease = false;

    public static final boolean VISTA_1756_Concessions_Should_Be_Hidden = true;

    public static final boolean VISTA_2242_Simple_Lease_Renewal = true;

    public static boolean VISTA_2256_Default_Product_Catalog_Show = false;

    public static boolean VISTA_2446_Periodic_Lease_Terms = false;

    public static boolean VISTA_3207_TENANT_SURE_YEARLY_PAY_SCHEDULE_IMPLEMENTED = false;

    public static boolean pendingYardiConfigPatchILS = true;

    public static boolean ILS_TestMode = true;

    //VISTA-3995  this is temporary regulation in Canada;  TODO change when going to US
    public static boolean visaDebitHasConvenienceFee = false;

    public static boolean yardi_unitOccupancySegments = true;
}
