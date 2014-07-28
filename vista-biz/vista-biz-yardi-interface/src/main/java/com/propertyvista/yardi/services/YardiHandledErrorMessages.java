/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 28, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.yardi.services;

public abstract class YardiHandledErrorMessages {

    public static final String errorMessage_NoAccess = "Invalid or no access to Yardi Property";

    public static final String errorMessage_TenantNotFound = "No tenants exist with the given search criteria";

    //-- payment reversal post messages

    public static final String errorMessage_AlreadyNSF1 = "May not  NSF  a receipt that has been NSF";

    public static final String errorMessage_AlreadyNSF2 = "May not  reverse  a receipt that has been NSF";

    public static final String errorMessage_AlreadyReversed = "Receipt has already been reversed";

    public static final String errorMessage_PostMonthAccess1 = "Cannot  NSF  a receipt whose post month is outside your allowable range";

    public static final String errorMessage_PostMonthAccess2 = "Cannot  reverse  a receipt whose post month is outside your allowable range";

    public static final String[] unableToPostTerminalMessages = new String[] { errorMessage_AlreadyNSF1, errorMessage_AlreadyNSF2,
            errorMessage_AlreadyReversed, errorMessage_PostMonthAccess1, errorMessage_PostMonthAccess2 };

}
