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

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.crm.client.visor.IVisorController;
import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;
import com.propertyvista.crm.rpc.services.customer.PreauthorizedPaymentsVisorService;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.tenant.lease.Tenant;

public abstract class PreauthorizedPaymentsVisorController implements IVisorController {

    private static final I18n i18n = I18n.get(PreauthorizedPaymentsVisorController.class);

    private final PreauthorizedPaymentsVisorView visor = new PreauthorizedPaymentsVisorView(this);

    private final PreauthorizedPaymentsVisorService service = GWT.<PreauthorizedPaymentsVisorService> create(PreauthorizedPaymentsVisorService.class);

    private final Key tenantId;

    public PreauthorizedPaymentsVisorController(Key tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public void show(final IPane parentView) {
        visor.populate(new Command() {
            @Override
            public void execute() {
                visor.setCaption(i18n.tr("Setup Preauthorized Payments"));
                parentView.showVisor(visor);
            }
        });
    }

    public void retrieve(AsyncCallback<PreauthorizedPaymentsDTO> callback) {
        service.retrieve(callback, EntityFactory.createIdentityStub(Tenant.class, tenantId));
    }

    public void save(AsyncCallback<VoidSerializable> callback, PreauthorizedPaymentsDTO pads) {
        service.save(callback, pads);
    }

    /**
     * Implement in caller to get editing result.
     * 
     * @param pads
     *            - edited PADs
     * @return - allow/disable visor close
     */
    public abstract boolean onClose(List<PreauthorizedPayment> pads);
}
