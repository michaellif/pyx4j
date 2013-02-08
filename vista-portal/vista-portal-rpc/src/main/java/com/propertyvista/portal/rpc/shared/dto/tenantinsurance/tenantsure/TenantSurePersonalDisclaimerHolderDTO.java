/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Transient
public interface TenantSurePersonalDisclaimerHolderDTO extends IEntity {

    IPrimitive<String> terms();

    @NotNull
    @Caption(name = "I agree")
    IPrimitive<Boolean> isAgreed();

}
