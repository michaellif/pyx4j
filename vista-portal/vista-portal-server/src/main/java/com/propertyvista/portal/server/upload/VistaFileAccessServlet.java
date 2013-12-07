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

import com.propertyvista.domain.company.EmployeeSignature;
import com.propertyvista.domain.media.ApplicationDocumentFile;
import com.propertyvista.domain.tenant.CustomerPicture;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificateScan;
import com.propertyvista.server.domain.ApplicationDocumentBlob;
import com.propertyvista.server.domain.CustomerPictureBlob;
import com.propertyvista.server.domain.EmployeeSignatureBlob;
import com.propertyvista.server.domain.GeneralInsurancePolicyBlob;

@SuppressWarnings("serial")
public class VistaFileAccessServlet extends VistaAbstractFileAccessServlet {

    public VistaFileAccessServlet() {
        register(CustomerPicture.class, CustomerPictureBlob.class);
        register(InsuranceCertificateScan.class, GeneralInsurancePolicyBlob.class);
        register(EmployeeSignature.class, EmployeeSignatureBlob.class);
        register(ApplicationDocumentFile.class, ApplicationDocumentBlob.class);
    }
}
