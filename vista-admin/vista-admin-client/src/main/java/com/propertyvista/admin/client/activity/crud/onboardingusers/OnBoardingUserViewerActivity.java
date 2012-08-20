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
package com.propertyvista.admin.client.activity.crud.onboardingusers;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.admin.client.activity.crud.AdminViewerActivity;
import com.propertyvista.admin.client.ui.crud.onboardingusers.OnboardingUserViewerView;
import com.propertyvista.admin.client.viewfactories.crud.ManagementVeiwFactory;
import com.propertyvista.admin.rpc.AdminSiteMap;
import com.propertyvista.admin.rpc.OnboardingUserDTO;
import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.admin.rpc.services.OnboardingUserCrudService;
import com.propertyvista.common.client.ui.components.security.PasswordChangeView;

public class OnBoardingUserViewerActivity extends AdminViewerActivity<OnboardingUserDTO> implements OnboardingUserViewerView.Presenter {

    public OnBoardingUserViewerActivity(CrudAppPlace place) {
        super(place, ManagementVeiwFactory.instance(OnboardingUserViewerView.class), GWT.<OnboardingUserCrudService> create(OnboardingUserCrudService.class));
    }

    @Override
    public void goToChangePassword(Key userId, String userName) {
        AppPlace passwordChangePlace = new AdminSiteMap.PasswordChange();
        passwordChangePlace.placeArg(PasswordChangeView.Presenter.PRINCIPAL_PK_ARG, userId.toString());
        passwordChangePlace.placeArg(PasswordChangeView.Presenter.PRINCIPAL_NAME_ARG, userName);
        passwordChangePlace.placeArg(PasswordChangeView.Presenter.PRINCIPAL_CLASS, PasswordChangeView.Presenter.PrincipalClass.ONBOARDING_PMC.toString());
        AppSite.getPlaceController().goTo(passwordChangePlace);
    }

    @Override
    public void createPmc(OnboardingUserDTO onboardingUser) {
        PmcDTO newPmc = EntityFactory.create(PmcDTO.class);
        newPmc.person().name().firstName().setValue(onboardingUser.firstName().getValue());
        newPmc.person().name().lastName().setValue(onboardingUser.lastName().getValue());
        newPmc.email().setValue(onboardingUser.email().getValue());
        newPmc.onboardingAccountId().setValue(onboardingUser.onboardingAccountId().getValue());
        newPmc.createPmcForExistingOnboardingUserRequest().set(onboardingUser.createIdentityStub());
        AppSite.getPlaceController().goTo(new AdminSiteMap.Management.PMC().formNewItemPlace(newPmc));

    }

}
