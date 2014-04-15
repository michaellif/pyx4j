/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 23, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server;

import com.pyx4j.essentials.server.upload.AbstractUploadServlet;

import com.propertyvista.crm.server.services.CommunicationMessageAttachmentUploadServiceImpl;
import com.propertyvista.crm.server.services.MaintenanceRequestPictureUploadServiceImpl;
import com.propertyvista.crm.server.services.MediaUploadBuildingServiceImpl;
import com.propertyvista.crm.server.services.MediaUploadFloorplanServiceImpl;
import com.propertyvista.crm.server.services.NoteAttachmentUploadServiceImpl;
import com.propertyvista.crm.server.services.PmcDocumentFileUploadServiceImpl;
import com.propertyvista.crm.server.services.UpdateUploadServiceImpl;
import com.propertyvista.crm.server.services.admin.SiteImageResourceUploadServiceImpl;
import com.propertyvista.crm.server.services.building.LandlordMediaUploadServiceImpl;
import com.propertyvista.crm.server.services.customer.CustomerPictureCrmUploadServiceImpl;
import com.propertyvista.crm.server.services.customer.InsuranceCertificateScanCrmUploadServiceImpl;
import com.propertyvista.crm.server.services.customer.TenantPadFileUploadServiceImpl;
import com.propertyvista.crm.server.services.importer.ImportCrmUploadServiceImpl;
import com.propertyvista.crm.server.services.lease.IdentificationDocumentCrmUploadServiceImpl;
import com.propertyvista.crm.server.services.lease.LeaseApplicationDocumentUploadServiceImpl;
import com.propertyvista.crm.server.services.lease.LeaseTermAgreementDocumentUploadServiceImpl;
import com.propertyvista.crm.server.services.lease.ProofOfAssetDocumentCrmUploadServiceImpl;
import com.propertyvista.crm.server.services.lease.ProofOfIncomeDocumentCrmUploadServiceImpl;
import com.propertyvista.crm.server.services.legal.LegalLetterUploadServiceImpl;
import com.propertyvista.crm.server.services.organization.EmployeeSignatureUploadServiceImpl;
import com.propertyvista.operations.server.services.EncryptedStorageServicePrivateKeyUploadServiceImpl;
import com.propertyvista.operations.server.services.ImportUploadServiceImpl;
import com.propertyvista.operations.server.services.MerchantAccountFileUploadServiceImpl;
import com.propertyvista.portal.server.portal.prospect.services.IdentificationDocumentProspectUploadServiceImpl;
import com.propertyvista.portal.server.portal.prospect.services.ProofOfAssetDocumentProspectUploadServiceImpl;
import com.propertyvista.portal.server.portal.prospect.services.ProofOfIncomeDocumentProspectUploadServiceImpl;
import com.propertyvista.portal.server.portal.resident.services.CommunicationMessageAttachmentUploadPortalServiceImpl;
import com.propertyvista.portal.server.portal.resident.services.maintenance.MaintenanceRequestPictureUploadPortalServiceImpl;
import com.propertyvista.portal.server.portal.resident.services.services.InsuranceCertificateScanResidentUploadServiceImpl;
import com.propertyvista.portal.server.portal.shared.services.CustomerPicturePortalUploadServiceImpl;

@SuppressWarnings("serial")
public class VistaUploadServlet extends AbstractUploadServlet {

    public VistaUploadServlet() {
        register(ImportUploadServiceImpl.class);
        register(UpdateUploadServiceImpl.class);
        register(ImportCrmUploadServiceImpl.class);
        register(MediaUploadFloorplanServiceImpl.class);
        register(MediaUploadBuildingServiceImpl.class);
        register(MaintenanceRequestPictureUploadServiceImpl.class);
        register(SiteImageResourceUploadServiceImpl.class);
        register(NoteAttachmentUploadServiceImpl.class);
        register(CustomerPictureCrmUploadServiceImpl.class);
        register(CustomerPicturePortalUploadServiceImpl.class);
        register(MaintenanceRequestPictureUploadPortalServiceImpl.class);
        register(CommunicationMessageAttachmentUploadPortalServiceImpl.class);
        register(CommunicationMessageAttachmentUploadServiceImpl.class);
        register(InsuranceCertificateScanCrmUploadServiceImpl.class);
        register(InsuranceCertificateScanResidentUploadServiceImpl.class);
        register(PmcDocumentFileUploadServiceImpl.class);
        register(EncryptedStorageServicePrivateKeyUploadServiceImpl.class);
        register(TenantPadFileUploadServiceImpl.class);
        register(MerchantAccountFileUploadServiceImpl.class);
        register(EmployeeSignatureUploadServiceImpl.class);
        register(LandlordMediaUploadServiceImpl.class);

        register(ProofOfIncomeDocumentCrmUploadServiceImpl.class);
        register(ProofOfIncomeDocumentProspectUploadServiceImpl.class);

        register(IdentificationDocumentCrmUploadServiceImpl.class);
        register(IdentificationDocumentProspectUploadServiceImpl.class);

        register(ProofOfAssetDocumentProspectUploadServiceImpl.class);
        register(ProofOfAssetDocumentCrmUploadServiceImpl.class);

        register(LeaseTermAgreementDocumentUploadServiceImpl.class);
        register(LeaseApplicationDocumentUploadServiceImpl.class);

        register(LegalLetterUploadServiceImpl.class);
    }
}
