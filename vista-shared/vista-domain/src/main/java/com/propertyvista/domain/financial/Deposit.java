/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 30, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial;

import com.pyx4j.entity.shared.IEntity;

public interface Deposit extends IEntity {

    /**
     * Deposit Type enum - "Rental" (for instance, for Last or Any Month or period - to use as Last Month Deposit for Ph1
     * "Security Deposit" - to be applied as part of the Revised Final bill or manually initiated by PMC
     * Deposit Apply Date date (for future releases, currently last billing month)
     * Deposit Interest boolean (deposit can be interest bearing or not)
     * GL (Deposits may have separate GLcode)
     * Tax (as for PIT or LeaseAdjReasons)
     * 
     * @author Alexs
     * 
     */

}
