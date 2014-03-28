/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 3, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.validation.validators.lease;

import com.propertyvista.biz.validation.framework.validators.CompositeEntityValidator;
import com.propertyvista.biz.validation.framework.validators.NotNullValidator;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseParticipantInApprovedLeaseValidator<E extends LeaseTermParticipant<?>> extends CompositeEntityValidator<E> {

    public LeaseParticipantInApprovedLeaseValidator(Class<E> entityClassLiteral) {
        super(entityClassLiteral);
    }

    @Override
    protected void init() {
        bind(proto().role(), new NotNullValidator());

        if (!VistaFeatures.instance().yardiIntegration()) {
            bind(proto().leaseParticipant().customer().person().name().firstName(), new NotNullValidator());
        }
        if (!VistaFeatures.instance().yardiIntegration()) {
            bind(proto().leaseParticipant().customer().person().name().lastName(), new NotNullValidator());
        }

        // Screening is automatically finalized
        if (!ScreeningValidator.screeningIsAutomaticallyFinalized) {
            bind(proto().leaseParticipant().customer(), new ScreeningValidator());
        }
    }

}
