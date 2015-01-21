/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-04-03
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.crud.lease.legal;

import java.math.BigDecimal;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.visor.AbstractVisorViewer;

import com.propertyvista.crm.client.activity.crud.lease.legal.LeaseLegalStateController;
import com.propertyvista.domain.legal.LegalStatus.Status;
import com.propertyvista.dto.LeaseLegalStateDTO;
import com.propertyvista.dto.LegalStatusDTO;
import com.propertyvista.dto.LegalStatusN4DTO;

@Deprecated
public class LeaseLegalStateVisor extends AbstractVisorViewer<LeaseLegalStateDTO> {

    private static final I18n i18n = I18n.get(LeaseLegalStateVisor.class);

    public LeaseLegalStateVisor(LeaseLegalStateController controller) {
        super(controller);
        setCaption(i18n.tr("Legal"));
        setForm(new LeaseLegalStateForm(controller));
    }

    public void requestNewLegalStatus(final AsyncCallback<LegalStatusDTO> legalStatusUpdateCallback) {
        new LegalStatusTypeSelectorDialog() {
            @Override
            public void onSelected(Status status) {
                if (status == Status.N4) {
                    LegalStatusN4DTO initialValue = EntityFactory.create(LegalStatusN4DTO.class);
                    initialValue.status().setValue(status);

                    initialValue.cancellationThreshold().setValue(new BigDecimal("0.00")); // TODO set default value from policy
                    initialValue.expiryDate().setValue(null); // TODO set default value form policy
                    initialValue.terminationDate().setValue(null); // TODO set default value from policy and delivery type

                    new LegalStatusN4Dialog(initialValue) {
                        @Override
                        public void onSetLegalStatus(LegalStatusDTO legalStatus) {
                            legalStatusUpdateCallback.onSuccess(legalStatus);
                        };
                    }.show();
                } else {
                    LegalStatusDTO initialValue = EntityFactory.create(LegalStatusDTO.class);
                    initialValue.status().setValue(status);
                    new LegalStatusDialog(initialValue) {
                        @Override
                        public void onSetLegalStatus(LegalStatusDTO legalStatus) {
                            legalStatusUpdateCallback.onSuccess(legalStatus);
                        }
                    }.show();
                }
            }
        }.show();
    }
}
