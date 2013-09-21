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

import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureAgreementDTO;
import com.propertyvista.portal.rpc.portal.web.services.services.TenantSureAgreementService;
import com.propertyvista.portal.web.client.activity.AbstractEditorActivity;
import com.propertyvista.portal.web.client.ui.services.insurance.TenantSurePageView;
import com.propertyvista.portal.web.client.ui.services.insurance.TenantSurePageView.TenantSurePagePresenter;

public class TenantSurePageActivity extends AbstractEditorActivity<TenantSureAgreementDTO> implements TenantSurePagePresenter {

    public TenantSurePageActivity(AppPlace place) {
        super(TenantSurePageView.class, GWT.<TenantSureAgreementService> create(TenantSureAgreementService.class), place);
    }

}
