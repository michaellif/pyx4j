/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-28
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance.errors;

public class TooManyPreviousClaimsException extends CfcApiException {

    private static final long serialVersionUID = 8788732518052939369L;

    public TooManyPreviousClaimsException(String errorCodeLine) {
        super(errorCodeLine);
        if (!isTooManyPreviousClaimsMessage(errorCodeLine)) {
            throw new IllegalArgumentException("This is not too many errors code: " + errorCodeLine);
        }
    }

    public static boolean isTooManyPreviousClaimsMessage(String errorCodeLine) {
        return "Unable to issue a quote due to high number of previous claims.".equals(parseCfcErrorMessage(errorCodeLine));
    }
}
