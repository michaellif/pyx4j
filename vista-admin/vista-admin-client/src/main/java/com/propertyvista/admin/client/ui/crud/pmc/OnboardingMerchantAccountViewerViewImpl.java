/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 14, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.pmc;

import com.propertyvista.admin.client.ui.crud.AdminViewerViewImplBase;
import com.propertyvista.admin.domain.pmc.OnboardingMerchantAccount;
import com.propertyvista.admin.rpc.AdminSiteMap;

public class OnboardingMerchantAccountViewerViewImpl extends AdminViewerViewImplBase<OnboardingMerchantAccount> implements OnboardingMerchantAccountViewerView {

    public OnboardingMerchantAccountViewerViewImpl() {
        super(AdminSiteMap.Management.OnboardingMerchantAccounts.class);
        setForm(new OnboardingMerchantAccountForm(true));
    }

}
