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
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity.services.insurance;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.site.shared.domain.Notification.NotificationType;

import com.propertyvista.portal.rpc.portal.web.dto.insurance.GeneralInsurancePolicyDTO;
import com.propertyvista.portal.rpc.portal.web.services.services.GeneralInsurancePolicyCrudService;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.activity.AbstractWizardCrudActivity;
import com.propertyvista.portal.web.client.ui.services.insurance.GeneralCertificateUploadWizardView;
import com.propertyvista.portal.web.client.ui.services.insurance.GeneralCertificateUploadWizardView.GeneralCertificateUploadWizardPresenter;

public class GeneralCertificateUploadWizardActivity extends AbstractWizardCrudActivity<GeneralInsurancePolicyDTO> implements
        GeneralCertificateUploadWizardPresenter {

    private static final I18n i18n = I18n.get(GeneralCertificateUploadWizardActivity.class);

    public GeneralCertificateUploadWizardActivity(AppPlace place) {
        super(GeneralCertificateUploadWizardView.class, GWT.<GeneralInsurancePolicyCrudService> create(GeneralInsurancePolicyCrudService.class),
                GeneralInsurancePolicyDTO.class);
    }

    @Override
    protected void onFinish(Key result) {
        Notification message = new Notification(null, i18n.tr("Certificate has been submitted sucsesfully"), NotificationType.INFO);
        PortalWebSite.getPlaceController().showNotification(message);
    }
}
