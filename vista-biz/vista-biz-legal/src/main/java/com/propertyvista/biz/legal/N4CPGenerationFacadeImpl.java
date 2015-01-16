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

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.legal.forms.framework.filling.FormFillerImpl;
import com.propertyvista.biz.legal.forms.n4cp.N4CPFieldsMapping;
import com.propertyvista.domain.legal.n4.pdf.N4FormFieldsData;
import com.propertyvista.domain.legal.n4cp.pdf.N4CPFormFieldsData;

public class N4CPGenerationFacadeImpl implements N4CPGenerationFacade {

    private static final String N4_CP_FORM_FILE = "n4cp.pdf";

    private final InternalBillingInvoiceDebitFetcherImpl invoiceDebitFetcher;

    public N4CPGenerationFacadeImpl() {
        invoiceDebitFetcher = new InternalBillingInvoiceDebitFetcherImpl();
    }

    @Override
    public byte[] generateN4CPLetter(N4CPFormFieldsData formData) {
        byte[] filledForm = null;
        try {
            byte[] formTemplate = IOUtils.toByteArray(N4CPGenerationFacadeImpl.class.getResourceAsStream(N4_CP_FORM_FILE));
            filledForm = new FormFillerImpl().fillForm(formTemplate, new N4CPFieldsMapping(), formData, true);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return filledForm;
    }

    @Override
    public N4CPFormFieldsData prepareN4CPData(N4FormFieldsData n4) {

        N4CPFormFieldsData n4cp = EntityFactory.create(N4CPFormFieldsData.class);
        n4cp.from().setValue(n4.from().getValue());
        n4cp.to().setValue(n4.to().getValue());
        n4cp.terminationDate().setValue(n4.terminationDate().getValue());
        n4cp.totalRentOwed().setValue(n4.totalRentOwed().getValue());

        n4cp.rentalUnitAddress().streetNumber().setValue(n4.rentalUnitAddress().streetNumber().getValue());
        n4cp.rentalUnitAddress().streetName().setValue(n4.rentalUnitAddress().streetName().getValue());
        n4cp.rentalUnitAddress().streetType().setValue(n4.rentalUnitAddress().streetType().getValue());
        n4cp.rentalUnitAddress().direction().setValue(n4.rentalUnitAddress().direction().getValue());
        n4cp.rentalUnitAddress().unit().setValue(n4.rentalUnitAddress().unit().getValue());
        n4cp.rentalUnitAddress().municipality().setValue(n4.rentalUnitAddress().municipality().getValue());
        n4cp.rentalUnitAddress().postalCode().setValue(n4.rentalUnitAddress().postalCode().getValue());

        n4cp.owedRent().totalRentOwing().setValue(n4.owedRent().totalRentOwing().getValue());
        n4cp.owedRent().rentOwingBreakdown().setValue(n4.owedRent().rentOwingBreakdown().getValue());

        n4cp.signature().signedBy().setValue(n4.signature().signedBy().getValue());
        n4cp.signature().signature().setValue(n4.signature().signature().getValue());
        n4cp.signature().signatureDate().setValue(n4.signature().signatureDate().getValue());

        n4cp.landlordsContactInfo().firstName().setValue(n4.landlordsContactInfo().firstName().getValue());
        n4cp.landlordsContactInfo().lastName().setValue(n4.landlordsContactInfo().lastName().getValue());
        n4cp.landlordsContactInfo().companyName().setValue(n4.landlordsContactInfo().companyName().getValue());
        n4cp.landlordsContactInfo().mailingAddress().setValue(n4.landlordsContactInfo().mailingAddress().getValue());
        n4cp.landlordsContactInfo().unit().setValue(n4.landlordsContactInfo().unit().getValue());
        n4cp.landlordsContactInfo().municipality().setValue(n4.landlordsContactInfo().municipality().getValue());
        n4cp.landlordsContactInfo().province().setValue(n4.landlordsContactInfo().province().getValue());
        n4cp.landlordsContactInfo().postalCode().setValue(n4.landlordsContactInfo().postalCode().getValue());
        n4cp.landlordsContactInfo().phoneNumber().setValue(n4.landlordsContactInfo().phoneNumber().getValue());
        n4cp.landlordsContactInfo().faxNumber().setValue(n4.landlordsContactInfo().faxNumber().getValue());
        n4cp.landlordsContactInfo().email().setValue(n4.landlordsContactInfo().email().getValue());

        return n4cp;

    }
}