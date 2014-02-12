/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-02-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.lease.agreement;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.site.client.ui.visor.IVisorEditor;

import com.propertyvista.crm.client.ui.crud.lease.LeaseViewerView;
import com.propertyvista.crm.client.ui.crud.lease.agreement.LeaseAgreementDocumentSigningVisor;
import com.propertyvista.dto.LeaseAgreementDocumentsSigningDTO;

public class LeaseAgreementDocumentSigningController implements IVisorEditor.Controller {

    private final LeaseAgreementDocumentSigningVisor visor;

    private final LeaseViewerView view;

    public LeaseAgreementDocumentSigningController(LeaseViewerView view) {
        this.visor = new LeaseAgreementDocumentSigningVisor(this);
        this.view = view;
    }

    @Override
    public void show() {
        this.visor.populate(EntityFactory.create(LeaseAgreementDocumentsSigningDTO.class));
        this.view.showVisor(this.visor);
    }

    @Override
    public void hide() {
        this.view.hideVisor();
    }

    @Override
    public void apply() {
        // TODO 
    }

    @Override
    public void save() {
        // TODO Auto-generated method stub
    }

}
