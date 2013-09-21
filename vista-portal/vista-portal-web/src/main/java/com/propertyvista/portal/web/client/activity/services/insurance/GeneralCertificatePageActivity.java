/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 21, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity.services.insurance;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.rpc.portal.web.dto.insurance.GeneralInsuranceCertificateDTO;
import com.propertyvista.portal.rpc.portal.web.services.services.GeneralInsuranceCertificateCrudService;
import com.propertyvista.portal.web.client.activity.AbstractEditorActivity;
import com.propertyvista.portal.web.client.ui.services.insurance.GeneralCertificatePageView;
import com.propertyvista.portal.web.client.ui.services.insurance.GeneralCertificatePageView.GeneralCertificatePagePresenter;

public class GeneralCertificatePageActivity extends AbstractEditorActivity<GeneralInsuranceCertificateDTO> implements GeneralCertificatePagePresenter {

    public GeneralCertificatePageActivity(AppPlace place) {
        super(GeneralCertificatePageView.class, GWT.<GeneralInsuranceCertificateCrudService> create(GeneralInsuranceCertificateCrudService.class), place);
    }

}
