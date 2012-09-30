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

public interface VistaTODO {

    /*
     * This is changed by BuildMaster when creating Branch.
     * Used by configurations to enable dual development environments.
     */
    public static final boolean codeBaseIsProdBranch = false;

    /**
     * Set <code>true</code> to let PtApp show a demo wizard for approved tenants
     */
    public static final boolean enableWelcomeWizardDemoMode = true;

    /**
     * Check that lease from is in the future compared to transaction time (currently if is true billing tests fail)
     */
    public static final boolean checkLeaseDatesOnUnitReservation = false;

    public static final boolean isAfterBeta04Version = false;

    public static boolean removedForProduction = false;

    public static boolean operationDataRemovedForProduction = false;

    public static boolean vladsLeaseMigration = true;

    /** joins with conditions */
    public final boolean complextQueryCriteria = true;

    public final boolean VISTA_1588 = true;

    public final boolean Equifax_Short_VISTA_478 = false;

    public final boolean Equifax_Long_VISTA_478 = true;

}
