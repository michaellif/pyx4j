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
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.activity.AbstractVisorController;
import com.pyx4j.site.client.ui.IPane;
import com.pyx4j.site.client.ui.visor.IVisorEditor;

import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;
import com.propertyvista.crm.rpc.services.customer.PreauthorizedPaymentsVisorService;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.PreauthorizedPaymentDTO;

public class PreauthorizedPaymentsVisorController extends AbstractVisorController implements IVisorEditor.Controller {

    private static final I18n i18n = I18n.get(PreauthorizedPaymentsVisorController.class);

    private final PreauthorizedPaymentsVisorView visor;

    private final PreauthorizedPaymentsVisorService service;

    private final Key tenantId;

    public PreauthorizedPaymentsVisorController(IPane parentView, Key tenantId) {
        super(parentView);
        this.tenantId = tenantId;
        visor = new PreauthorizedPaymentsVisorView(this);
        service = GWT.<PreauthorizedPaymentsVisorService> create(PreauthorizedPaymentsVisorService.class);
    }

    @Override
    public void show() {
        visor.populate(new Command() {
            @Override
            public void execute() {
                visor.setCaption(i18n.tr("Setup Pre-Authorized Payments"));
                getParentView().showVisor(visor);
            }
        });
    }

    public void retrieve(AsyncCallback<PreauthorizedPaymentsDTO> callback) {
        service.retrieve(callback, EntityFactory.createIdentityStub(Tenant.class, tenantId));
    }

    public void save(AsyncCallback<VoidSerializable> callback, PreauthorizedPaymentsDTO pads) {
        service.save(callback, pads);
    }

    void create(AsyncCallback<PreauthorizedPaymentDTO> callback) {
        service.create(callback, EntityFactory.createIdentityStub(Tenant.class, tenantId));
    }

    /**
     * Override in caller to get editing result.
     * 
     * @param pads
     *            - edited PADs
     * @return - allow/disable visor close
     */
    public boolean onClose(List<AutopayAgreement> pads) {
        return true;
    }

    @Override
    public void apply() {
        save(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
            }
        }, visor.getValue());
    }

    @Override
    public void save() {
        save(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                service.recollect(new DefaultAsyncCallback<Vector<AutopayAgreement>>() {
                    @Override
                    public void onSuccess(Vector<AutopayAgreement> result) {
                        if (onClose(result)) {
                            getParentView().hideVisor();
                        }
                    }
                }, EntityFactory.createIdentityStub(Tenant.class, tenantId));
            }
        }, visor.getValue());
    }
}
