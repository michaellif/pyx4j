/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-14
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.visor.paps;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.crm.client.visor.IVisorController;
import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;
import com.propertyvista.crm.rpc.services.customer.PreauthorizedPaymentsVisorService;
import com.propertyvista.domain.tenant.lease.Tenant;

public class PreauthorizedPaymentsVisorController implements IVisorController {

    private static final I18n i18n = I18n.get(PreauthorizedPaymentsVisorController.class);

    private final PreauthorizedPaymentsVisorView visor = new PreauthorizedPaymentsVisorView(this);

    private final Tenant tenantId;

    public PreauthorizedPaymentsVisorController(Tenant tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public void show(final IPane parentView) {
        visor.populate(new Command() {
            @Override
            public void execute() {
                parentView.showVisor(visor, i18n.tr("Preauthorized Payments"));
            }
        });
    }

    public void populate(AsyncCallback<PreauthorizedPaymentsDTO> callback) {
        GWT.<PreauthorizedPaymentsVisorService> create(PreauthorizedPaymentsVisorService.class).retrieve(callback, tenantId);
    }
}
