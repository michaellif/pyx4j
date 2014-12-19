/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 10, 2014
 * @author arminea
 */
package com.propertyvista.biz.legal;

import org.apache.commons.io.IOUtils;

import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.legal.forms.framework.filling.FormFillerImpl;
import com.propertyvista.biz.legal.forms.n4cs.N4CSFieldsMapping;
import com.propertyvista.domain.legal.n4.N4FormFieldsData;
import com.propertyvista.domain.legal.n4cs.N4CSDocumentType.DocumentType;
import com.propertyvista.domain.legal.n4cs.N4CSFormFieldsData;
import com.propertyvista.domain.legal.n4cs.N4CSServiceMethod.ServiceMethod;
import com.propertyvista.domain.legal.n4cs.N4CSSignature.SignedBy;
import com.propertyvista.domain.legal.n4cs.N4CSToPersonInfo.ToType;

public class N4CSGenerationFacadeImpl implements N4CSGenerationFacade {

    private static final String N4_CS_FORM_FILE = "n4cs.pdf";

    private final InternalBillingInvoiceDebitFetcherImpl invoiceDebitFetcher;

    public N4CSGenerationFacadeImpl() {
        invoiceDebitFetcher = new InternalBillingInvoiceDebitFetcherImpl();
    }

    @Override
    public byte[] generateN4CSLetter(N4CSFormFieldsData formData) {
        byte[] filledForm = null;
        try {
            byte[] formTemplate = IOUtils.toByteArray(N4CSGenerationFacadeImpl.class.getResourceAsStream(N4_CS_FORM_FILE));
            filledForm = new FormFillerImpl().fillForm(formTemplate, new N4CSFieldsMapping(), formData, true);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return filledForm;
    }

    @Override
    public N4CSFormFieldsData prepareN4CSData(N4FormFieldsData n4, ServiceMethod serviceMethod) {

        N4CSFormFieldsData n4cs = EntityFactory.create(N4CSFormFieldsData.class);
        n4cs.reporter().setValue(n4.landlordsContactInfo().firstName().getValue() + " " + n4.landlordsContactInfo().lastName().getValue());
        n4cs.document().termination().setValue("N4");
        n4cs.document().docType().setValue(DocumentType.TT);
        StringBuilder address = new StringBuilder();
        if (n4.rentalUnitAddress().streetNumber().getValue() != null && !n4.rentalUnitAddress().streetNumber().getValue().equals("")) {
            address.append(n4.rentalUnitAddress().streetNumber().getValue());
        }
        if (n4.rentalUnitAddress().streetName().getValue() != null && !n4.rentalUnitAddress().streetName().getValue().equals("")) {
            address.append(" " + n4.rentalUnitAddress().streetName().getValue());
        }
        if (n4.rentalUnitAddress().streetType().getValue() != null && !n4.rentalUnitAddress().streetType().getValue().equals("")) {
            address.append(" " + n4.rentalUnitAddress().streetType().getValue());
        }
        if (n4.rentalUnitAddress().direction().getValue() != null && !n4.rentalUnitAddress().direction().getValue().equals("")) {
            address.append(" " + n4.rentalUnitAddress().direction().getValue());
        }

        n4cs.street().setValue(address.toString());
        n4cs.unit().setValue(n4.rentalUnitAddress().unit().getValue());
        n4cs.municipality().setValue(n4.rentalUnitAddress().municipality().getValue());
        n4cs.postalCode().setValue(n4.rentalUnitAddress().postalCode().getValue());
        n4cs.issueDate().setValue(SystemDateManager.getLogicalDate());
        n4cs.signature().firstname().setValue(n4.landlordsContactInfo().firstName().getValue());
        n4cs.signature().lastname().setValue(n4.landlordsContactInfo().lastName().getValue());
        n4cs.signature().phone().setValue(n4.landlordsContactInfo().phoneNumber().getValue());
        n4cs.signature().signedBy().setValue(SignedBy.RA);
        n4cs.signature().signature().setValue(n4.signature().signature().getValue());
        n4cs.signature().signatureDate().setValue(SystemDateManager.getLogicalDate());
        n4cs.passedTo().tpType().setValue(ToType.Tenant);
        n4cs.passedTo().name().setValue(n4.to().getStringView());
        n4cs.service().method().setValue(serviceMethod);
        if (serviceMethod.equals(ServiceMethod.M)) {
            StringBuilder lastAddress = new StringBuilder(n4.rentalUnitAddress().unit().getValue());
            lastAddress.append(" - " + address);
            n4cs.service().lastAddr().setValue(lastAddress.toString());

        }

        return n4cs;

    }
}