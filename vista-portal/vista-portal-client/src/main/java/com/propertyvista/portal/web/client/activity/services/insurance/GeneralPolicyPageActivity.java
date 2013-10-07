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

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.site.shared.domain.Notification.NotificationType;

import com.propertyvista.portal.rpc.portal.web.dto.insurance.GeneralInsurancePolicyDTO;
import com.propertyvista.portal.rpc.portal.web.services.services.GeneralInsurancePolicyCrudService;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.activity.AbstractEditorActivity;
import com.propertyvista.portal.web.client.ui.services.insurance.GeneralPolicyPageView;
import com.propertyvista.portal.web.client.ui.services.insurance.GeneralPolicyPageView.GeneralPolicyPagePresenter;

public class GeneralPolicyPageActivity extends AbstractEditorActivity<GeneralInsurancePolicyDTO> implements GeneralPolicyPagePresenter {

    private static final I18n i18n = I18n.get(GeneralPolicyPageActivity.class);

    public GeneralPolicyPageActivity(AppPlace place) {
        super(GeneralPolicyPageView.class, GWT.<GeneralInsurancePolicyCrudService> create(GeneralInsurancePolicyCrudService.class), place);
    }

    @Override
    public void remove() {
        getService().delete(new DefaultAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Notification message = new Notification(i18n.tr("Certificate has been removed successfully!"), "", NotificationType.INFO);
                PortalWebSite.getPlaceController().showNotification(message);
            }
        }, getView().getValue().certificate().getPrimaryKey());
    }

}
