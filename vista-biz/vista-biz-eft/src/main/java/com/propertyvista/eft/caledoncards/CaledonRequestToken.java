/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 28, 2011
 * @author kostya
 */
package com.propertyvista.eft.caledoncards;

public class CaledonRequestToken extends CaledonRequest {

    @HttpRequestField("TOKEN_ACTION")
    public String tokenAction;

    @HttpRequestField("TOKEN_REF")
    public String tokenRef;

}
