/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-11
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.resident.activity.financial.autopay;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.dialog.ConfirmDecline;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.portal.resident.ui.financial.autopay.AutoPayView;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.AutoPayDTO;
import com.propertyvista.portal.rpc.portal.resident.services.financial.AutoPayWizardService;
import com.propertyvista.portal.rpc.portal.resident.services.financial.PaymentService;
import com.propertyvista.portal.shared.activity.AbstractEditorActivity;

public class AutoPayActivity extends AbstractEditorActivity<AutoPayDTO> implements AutoPayView.Presenter {

    private static final I18n i18n = I18n.get(AutoPayActivity.class);

    public AutoPayActivity(AppPlace place) {
        super(AutoPayView.class, GWT.<AutoPayWizardService> create(AutoPayWizardService.class), place);
    }

    @Override
    public void save() {
        final AutoPayDTO value = getView().getValue();

        if (value.total().getValue().signum() > 0) {
            super.save();
        } else {
            MessageDialog.confirm(i18n.tr("Auto Pay Agreement"), i18n.tr("There are no payments set! Would you like rather to delete the Auto Pay?"),
                    new ConfirmDecline() {
                        @Override
                        public void onConfirmed() {
                            GWT.<PaymentService> create(PaymentService.class).deleteAutoPay(new DefaultAsyncCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean result) {
                                    getView().reset(); //  to avoid navigation out of non-saved data message...
                                    AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Financial());
                                }
                            }, EntityFactory.createIdentityStub(AutopayAgreement.class, value.getPrimaryKey()));
                        }

                        @Override
                        public void onDeclined() {
                            // do nothing...
                        }
                    });
        }
    }
}
