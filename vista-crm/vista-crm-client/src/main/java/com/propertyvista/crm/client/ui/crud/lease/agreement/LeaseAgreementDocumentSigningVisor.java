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
package com.propertyvista.crm.client.ui.crud.lease.agreement;

import java.util.List;

import com.pyx4j.site.client.ui.visor.AbstractVisorEditor;
import com.pyx4j.site.client.ui.visor.IVisorEditor;

import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseAgreementDocumentsSigningDTO;

public class LeaseAgreementDocumentSigningVisor extends AbstractVisorEditor<LeaseAgreementDocumentsSigningDTO> {

    private LeaseAgreementDocumentSigningForm form;

    public LeaseAgreementDocumentSigningVisor(IVisorEditor.Controller controller) {
        super(controller);
        setForm(form = new LeaseAgreementDocumentSigningForm());
    }

    public void setParticipantsOptions(List<LeaseTermParticipant<?>> participantsOptions) {
        form.setLeaseTermParticipantsOptions(participantsOptions);
    }

}
