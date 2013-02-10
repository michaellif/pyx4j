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
package com.propertyvista.operations.client.ui.crud.pmc;

import com.propertyvista.operations.client.ui.crud.OperationsEditorViewImplBase;
import com.propertyvista.operations.rpc.OperationsSiteMap;
import com.propertyvista.operations.rpc.OnboardingMerchantAccountDTO;

public class OnboardingMerchantAccountEditorViewImpl extends OperationsEditorViewImplBase<OnboardingMerchantAccountDTO> implements
        OnboardingMerchantAccountEditorView {

    public OnboardingMerchantAccountEditorViewImpl() {
        super(OperationsSiteMap.Management.OnboardingMerchantAccounts.class);
        setForm(new OnboardingMerchantAccountForm(this));
    }
}
