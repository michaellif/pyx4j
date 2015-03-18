/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-03-19
 * @author ArtyomB
 */
package com.propertyvista.dto.leaseapplicationdocument;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;

@Transient
public interface LeaseApplicationDocumentDataSectionsDTO extends IEntity {

    IList<LeaseApplicationDocumentDataLeaseSectionDTO> leaseSection();

    IList<LeaseApplicationDocumentDataRentalItemsSectionDTO> rentalItemsSection();

    IList<LeaseApplicationDocumentDataAdjustmentsSectionDTO> adjustmentsSection();

    IList<LeaseApplicationDocumentDataFirstPaymentSectionDTO> firstPaymentSection();

    IList<LeaseApplicationDocumentDataPeopleSectionDTO> peopleSection();

    IList<LeaseApplicationDocumentDataAboutYouSectionDTO> aboutYouSection();

    IList<LeaseApplicationDocumentDataAdditionalInfoSectionDTO> additionalInfoSection();

    IList<LeaseApplicationDocumentDataFinancialSectionDTO> financialSection();

    IList<LeaseApplicationDocumentDataEmergencyContactsSectionDTO> emergencyContactsSection();

    IList<LeaseApplicationDocumentDataLegalSectionDTO> legalSection();

    IList<LeaseApplicationDocumentDataConfirmationSectionDTO> confirmationSection();

}
