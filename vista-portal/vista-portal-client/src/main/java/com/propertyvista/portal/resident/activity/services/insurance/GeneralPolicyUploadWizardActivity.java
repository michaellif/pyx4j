/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author VladL
 */
package com.propertyvista.portal.resident.activity.services.insurance;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.site.shared.domain.Notification.NotificationType;

import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.services.insurance.GeneralPolicyUploadWizardView;
import com.propertyvista.portal.resident.ui.services.insurance.GeneralPolicyUploadWizardView.GeneralPolicyUploadWizardPresenter;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.GeneralInsurancePolicyDTO;
import com.propertyvista.portal.rpc.portal.resident.services.insurance.GeneralInsurancePolicyCrudService;
import com.propertyvista.portal.shared.activity.AbstractWizardCrudActivity;

public class GeneralPolicyUploadWizardActivity extends AbstractWizardCrudActivity<GeneralInsurancePolicyDTO, GeneralPolicyUploadWizardView> implements
        GeneralPolicyUploadWizardPresenter {

    private static final I18n i18n = I18n.get(GeneralPolicyUploadWizardActivity.class);

    public GeneralPolicyUploadWizardActivity(AppPlace place) {
        super(GeneralPolicyUploadWizardView.class, GWT.<GeneralInsurancePolicyCrudService> create(GeneralInsurancePolicyCrudService.class),
                GeneralInsurancePolicyDTO.class);
    }

    @Override
    protected void onFinish(Key result) {
        Notification message = new Notification(null, i18n.tr("Certificate has been submitted sucsesfully"), NotificationType.INFO);
        ResidentPortalSite.getPlaceController().showNotification(message);
    }
}
