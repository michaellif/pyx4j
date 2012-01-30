/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Owned;

import com.propertyvista.domain.policy.BuildingPolicy;
import com.propertyvista.domain.policy.policies.specials.EmailTemplate;

@DiscriminatorValue("EmailTemplatesPolicy")
public interface EmailTemplatesPolicy extends BuildingPolicy {

    @Owned
    EmailTemplate passwordRetrievalCrm();

    @Owned
    EmailTemplate passwordRetrievalTenant();

    @Owned
    EmailTemplate applicationCreatedApplicant();

    @Owned
    EmailTemplate applicationCreatedCoApplicant();

    @Owned
    EmailTemplate applicationCreatedGuarantor();

    @Owned
    EmailTemplate applicationApproved();

    @Owned
    EmailTemplate applicationDeclined();

}
