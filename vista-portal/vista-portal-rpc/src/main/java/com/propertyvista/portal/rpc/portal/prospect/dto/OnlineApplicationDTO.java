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
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepStatus;
import com.propertyvista.domain.tenant.prospect.SignedOnlineApplicationConfirmationTerm;
import com.propertyvista.domain.tenant.prospect.SignedOnlineApplicationLegalTerm;
import com.propertyvista.domain.tenant.prospect.OnlineApplication.Status;

@Transient
public interface OnlineApplicationDTO extends IEntity {

    //--------------------------------------------
    // read-only data:

    AptUnit unit();

    @Caption(name = "Included Utilities")
    IPrimitive<String> utilities();

    IPrimitive<LogicalDate> leaseFrom();

    IPrimitive<LogicalDate> leaseTo();

    BillableItem selectedService();

    IList<BillableItem> selectedFeatures();

    /** Sets the age of majority for validation, null if no validation is required */
    IPrimitive<Integer> ageOfMajority();

    IPrimitive<Boolean> enforceAgeOfMajority();

    IPrimitive<Boolean> maturedOccupantsAreApplicants();

    //--------------------------------------------
    // read and update data:

    UnitSelectionDTO unitSelection();

    UnitOptionsSelectionDTO unitOptionsSelection();

    ApplicantDTO applicant();

    IList<CoapplicantDTO> coapplicants();

    IList<GuarantorDTO> guarantors();

    IList<SignedOnlineApplicationLegalTerm> legalTerms();

    IList<SignedOnlineApplicationConfirmationTerm> confirmationTerms();

    IList<OnlineApplicationWizardStepStatus> stepsStatuses();

    PaymentDTO payment();

    IPrimitive<Status> status();
}
