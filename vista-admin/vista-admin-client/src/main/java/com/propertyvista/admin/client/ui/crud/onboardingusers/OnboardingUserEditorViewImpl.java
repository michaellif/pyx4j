/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.onboardingusers;

import com.propertyvista.admin.client.ui.crud.AdminEditorViewImplBase;
import com.propertyvista.admin.rpc.AdminSiteMap;
import com.propertyvista.admin.rpc.OnboardingUserDTO;

public class OnboardingUserEditorViewImpl extends AdminEditorViewImplBase<OnboardingUserDTO> implements OnboardingUserEditorView {

    public OnboardingUserEditorViewImpl() {
        super(AdminSiteMap.Management.OnboardingUsers.class);
        setForm(new OnboardingUserForm());
    }

}
