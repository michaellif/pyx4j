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

    /**
     * Set <code>true</code> to let PtApp show a demo wizard for approved tenants
     */
    public final static boolean enableWelcomeWizardDemoMode = true;

    public static boolean removedForProduction = false;

    // e.g. person().name()
    public final boolean entityAsStringQueryCriteria = true;

    /** joins with conditions */
    public final boolean complextQueryCriteria = true;

}
