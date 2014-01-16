/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.policy.framework.LowestApplicableNode;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.framework.Policy;

@DiscriminatorValue("N4Policy")
//TODO in order to change Lowest Applicable Node there must be a UI and whole N4 generation infrastructure that allows to set up the defaults listed here on lease level
//      right now there's nothing like that
@LowestApplicableNode(value = OrganizationPoliciesNode.class)
public interface N4Policy extends Policy {

    @Caption(description = "Signature image taken from Employee's profile")
    IPrimitive<Boolean> includeSignature();

    @NotNull
    IPrimitive<String> companyName();

    @EmbeddedEntity
    AddressStructured mailingAddress();

    @Editor(type = EditorType.phone)
    IPrimitive<String> phoneNumber();

    @Editor(type = EditorType.phone)
    IPrimitive<String> faxNumber();

    @Editor(type = EditorType.email)
    IPrimitive<String> emailAddress();

    @RpcTransient
    IList<ARCode> relevantARCodes();

    @NotNull
    IPrimitive<Integer> handDeliveryAdvanceDays();

    @NotNull
    IPrimitive<Integer> mailDeliveryAdvanceDays();

    @NotNull
    IPrimitive<Integer> courierDeliveryAdvanceDays();

}
