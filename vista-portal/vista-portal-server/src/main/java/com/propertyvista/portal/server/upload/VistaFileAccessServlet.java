/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 30, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.portal.server.upload;

import com.propertyvista.domain.blob.CustomerPictureBlob;
import com.propertyvista.domain.blob.EmployeeSignatureBlob;
import com.propertyvista.domain.blob.IdentificationDocumentBlob;
import com.propertyvista.domain.blob.InsuranceCertificateScanBlob;
import com.propertyvista.domain.blob.LandlordMediaBlob;
import com.propertyvista.domain.blob.LeaseTermAgreementDocumentBlob;
import com.propertyvista.domain.blob.LegalLetterBlob;
import com.propertyvista.domain.blob.MaintenanceRequestPictureBlob;
import com.propertyvista.domain.blob.NoteAttachmentBlob;
import com.propertyvista.domain.blob.ProofOfEmploymentDocumentBlob;
import com.propertyvista.domain.blob.operations.PmcDocumentBlob;
import com.propertyvista.domain.company.EmployeeSignature;
import com.propertyvista.domain.legal.LegalLetter;
import com.propertyvista.domain.maintenance.MaintenanceRequestPicture;
import com.propertyvista.domain.media.IdentificationDocumentFile;
import com.propertyvista.domain.media.ProofOfEmploymentDocumentFile;
import com.propertyvista.domain.note.NoteAttachment;
import com.propertyvista.domain.pmc.info.PmcDocumentFile;
import com.propertyvista.domain.property.LandlordMedia;
import com.propertyvista.domain.tenant.CustomerPicture;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificateScan;
import com.propertyvista.domain.tenant.lease.LeaseTermAgreementDocument;

@SuppressWarnings("serial")
public class VistaFileAccessServlet extends VistaAbstractFileAccessServlet {

    public VistaFileAccessServlet() {
        register(CustomerPicture.class, CustomerPictureBlob.class);
        register(MaintenanceRequestPicture.class, MaintenanceRequestPictureBlob.class);
        register(InsuranceCertificateScan.class, InsuranceCertificateScanBlob.class);
        register(EmployeeSignature.class, EmployeeSignatureBlob.class);
        register(ProofOfEmploymentDocumentFile.class, ProofOfEmploymentDocumentBlob.class);
        register(IdentificationDocumentFile.class, IdentificationDocumentBlob.class);
        register(LegalLetter.class, LegalLetterBlob.class);
        register(LeaseTermAgreementDocument.class, LeaseTermAgreementDocumentBlob.class);
        register(LandlordMedia.class, LandlordMediaBlob.class);

        register(NoteAttachment.class, NoteAttachmentBlob.class);

        register(PmcDocumentFile.class, PmcDocumentBlob.class);
    }
}
