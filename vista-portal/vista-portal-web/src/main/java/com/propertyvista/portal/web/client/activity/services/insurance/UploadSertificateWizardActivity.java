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

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.rpc.portal.web.dto.insurance.OtherProviderInsuranceSertificateDTO;
import com.propertyvista.portal.rpc.portal.web.services.services.UploadInsuranceSertificateWizardService;
import com.propertyvista.portal.web.client.activity.AbstractWizardActivity;
import com.propertyvista.portal.web.client.ui.services.insurance.UploadSertificateWizardView;
import com.propertyvista.portal.web.client.ui.services.insurance.UploadSertificateWizardView.UploadSertificateWizardPresenter;

public class UploadSertificateWizardActivity extends AbstractWizardActivity<OtherProviderInsuranceSertificateDTO> implements UploadSertificateWizardPresenter {

    public UploadSertificateWizardActivity(AppPlace place) {
        super(UploadSertificateWizardView.class, GWT.<UploadInsuranceSertificateWizardService> create(UploadInsuranceSertificateWizardService.class),
                OtherProviderInsuranceSertificateDTO.class);
    }

}
