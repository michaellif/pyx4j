/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.prospect.dto;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.policy.policies.ProspectPortalPolicy.FeePayment;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepStatus;
import com.propertyvista.domain.tenant.prospect.SignedOnlineApplicationConfirmationTerm;
import com.propertyvista.domain.tenant.prospect.SignedOnlineApplicationLegalTerm;
import com.propertyvista.portal.rpc.portal.shared.dto.LandlordInfo;

@Transient
public interface OnlineApplicationDTO extends IEntity {

    //--------------------------------------------
    // read-only data:

    LandlordInfo landlordInfo();

    AptUnit unit();

    @Editor(type = EditorType.label)
    @Caption(name = "Included Utilities")
    IPrimitive<String> utilities();

    @Editor(type = EditorType.label)
    IPrimitive<LogicalDate> leaseFrom();

    @Editor(type = EditorType.label)
    IPrimitive<LogicalDate> leaseTo();

    IList<LeaseTermTenant> tenants();

    Building policyNode();

    /** Sets the age of majority for validation, null if no validation is required */
    IPrimitive<Integer> ageOfMajority();

    IPrimitive<Boolean> enforceAgeOfMajority();

    IPrimitive<Boolean> maturedOccupantsAreApplicants();

    IPrimitive<Boolean> noNeedGuarantors();

    IPrimitive<Integer> yearsToForcingPreviousAddress();

    IPrimitive<Boolean> emergencyContactsIsMandatory();

    IPrimitive<Integer> emergencyContactsAmount();

    //--------------------------------------------
    // read and update data:

    LeaseChargesDataDTO leaseChargesData();

    UnitSelectionDTO unitSelection();

    UnitOptionsSelectionDTO unitOptionsSelection();

    ApplicantDTO applicantData();

    Name applicant();

    IList<CoapplicantDTO> coapplicants();

    IList<DependentDTO> dependents();

    IList<GuarantorDTO> guarantors();

    IList<SignedOnlineApplicationLegalTerm> legalTerms();

    IList<SignedOnlineApplicationConfirmationTerm> confirmationTerms();

    IList<OnlineApplicationWizardStepStatus> stepsStatuses();

    IPrimitive<FeePayment> feePaymentPolicy();

    PaymentDTO payment();
}
