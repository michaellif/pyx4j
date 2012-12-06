/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-06
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance;

public class TenantSureTextFacadeImpl implements TenantSureTextFacade {

    @Override
    public String getPersonalDisclaimerText() {
        return "THIS IS PERSONAL DISCLAIMER";
    }

    @Override
    public String getFaq() {
        //@formatter:off        
        return "THIS IS FAQ";
        //@formatter:on
    }

}
