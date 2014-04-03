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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.legal;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.visor.AbstractVisorViewer;

import com.propertyvista.crm.client.activity.crud.lease.legal.LeaseLegalStateController;
import com.propertyvista.crm.client.ui.crud.lease.LegalStatusDialog;
import com.propertyvista.dto.LeaseLegalStateDTO;
import com.propertyvista.dto.LegalStatusDTO;

public class LeaseLegalStateVisor extends AbstractVisorViewer<LeaseLegalStateDTO> {

    private static final I18n i18n = I18n.get(LeaseLegalStateVisor.class);

    public LeaseLegalStateVisor(LeaseLegalStateController controller) {
        super(controller);
        setCaption(i18n.tr("Legal"));
        setForm(new LeaseLegalStateForm(controller));
    }

    public void requestNewLegalStatus(final AsyncCallback<LegalStatusDTO> legalStatusUpadate) {
        new LegalStatusDialog() {
            @Override
            public void onSetLegalStatus(LegalStatusDTO legalStatus) {
                legalStatusUpadate.onSuccess(legalStatus);
            }
        }.show();
    }
}
