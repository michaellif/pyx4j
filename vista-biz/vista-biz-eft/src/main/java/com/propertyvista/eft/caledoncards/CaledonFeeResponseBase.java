/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 11, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.caledoncards;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CaledonFeeResponseBase {

    /**
     * USed to log the error transactions
     */
    public String responseBody;

    @HttpResponseField(value = "type")
    @NotNull
    public String type;

    @HttpResponseField(value = "terminal_id")
    @NotNull
    @Size(max = 8)
    public String terminalID;

    @HttpResponseField("response_code")
    @NotNull
    public String responseCode;
}
