/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 24, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.domain.policy.dto;

import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.policy.framework.PolicyDTOBase;
import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentPaymentType;

@Transient
@ExtendsBO(value = IdAssignmentPolicy.class)
public interface IdAssignmentPolicyDTO extends PolicyDTOBase, IdAssignmentPolicy {

    IList<IdAssignmentItem> editableItems();

    @I18n(strategy = I18n.I18nStrategy.IgnoreMember)
    IdAssignmentPaymentType paymentTypesDefaults();

    @I18n(strategy = I18n.I18nStrategy.IgnoreMember)
    IPrimitive<Integer> yardiDocumentNumberLenght();

}
