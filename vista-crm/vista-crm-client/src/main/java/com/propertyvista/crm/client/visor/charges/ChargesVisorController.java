/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 6, 2012
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.visor.charges;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.site.client.activity.AbstractVisorController;
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.crm.rpc.services.billing.BillPreviewService;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.BillDTO;

public class ChargesVisorController extends AbstractVisorController {

    private final ChargesVisorView visor;

    private final Key leaseId;

    public ChargesVisorController(IPane parentView, Key leaseId) {
        super(parentView);
        this.leaseId = leaseId;
        visor = new ChargesVisorView(this);
    }

    @Override
    public void show() {
        visor.populate(new Command() {
            @Override
            public void execute() {
                getParentView().showVisor(visor);
            }
        });
    }

    public void populate(AsyncCallback<BillDTO> callback) {
        GWT.<BillPreviewService> create(BillPreviewService.class).getPreview(callback, EntityFactory.createIdentityStub(Lease.class, leaseId));
    }
}
