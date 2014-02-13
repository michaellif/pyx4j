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

import java.util.List;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.site.client.ui.visor.IVisorEditor;

import com.propertyvista.crm.client.ui.crud.lease.LeaseViewerView;
import com.propertyvista.crm.client.ui.crud.lease.agreement.LeaseAgreementDocumentSigningVisor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseAgreementDocumentsDTO;
import com.propertyvista.dto.LeaseAgreementStackholderSigningProgressDTO;

public class LeaseAgreementDocumentSigningController implements IVisorEditor.Controller {

    private final LeaseAgreementDocumentSigningVisor visor;

    private final LeaseViewerView view;

    private final List<LeaseTermParticipant<?>> leaseTermParticipantOptions;

    public LeaseAgreementDocumentSigningController(LeaseViewerView view, List<LeaseTermParticipant<?>> leaseTermParticipantOptions) {
        this.visor = new LeaseAgreementDocumentSigningVisor(this);
        this.view = view;
        this.leaseTermParticipantOptions = leaseTermParticipantOptions;
    }

    @Override
    public void show() {
        LeaseAgreementDocumentsDTO leaseAgreementDocuments = EntityFactory.create(LeaseAgreementDocumentsDTO.class);
        LeaseAgreementStackholderSigningProgressDTO stackholder1 = leaseAgreementDocuments.signingProgress().stackholdersProgressBreakdown().$();
        stackholder1.name().setValue("Jerry Sienfield");
        stackholder1.role().setValue("Applicant");
        stackholder1.hasSigned().setValue(true);

        LeaseAgreementStackholderSigningProgressDTO stackholder2 = leaseAgreementDocuments.signingProgress().stackholdersProgressBreakdown().$();
        stackholder2.name().setValue("George Costanza");
        stackholder2.role().setValue("Co-Applicant");
        stackholder2.hasSigned().setValue(false);

        LeaseAgreementStackholderSigningProgressDTO stackholder3 = leaseAgreementDocuments.signingProgress().stackholdersProgressBreakdown().$();
        stackholder3.name().setValue("Elane");
        stackholder3.role().setValue("Guarantor");
        stackholder3.hasSigned().setValue(false);

        leaseAgreementDocuments.signingProgress().stackholdersProgressBreakdown().add(stackholder1);
        leaseAgreementDocuments.signingProgress().stackholdersProgressBreakdown().add(stackholder2);
        leaseAgreementDocuments.signingProgress().stackholdersProgressBreakdown().add(stackholder3);

        this.visor.populate(leaseAgreementDocuments);
        this.view.showVisor(this.visor);
        this.visor.setParticipantsOptions(leaseTermParticipantOptions);
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
