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
package com.propertyvista.biz.validation.validators;

import com.propertyvista.biz.validation.framework.validators.CompositeEntityValidator;
import com.propertyvista.biz.validation.framework.validators.NotNullValidator;
import com.propertyvista.domain.tenant.Tenant;

public class TenantInApprovedLeaseValidator extends CompositeEntityValidator<Tenant> {

    public TenantInApprovedLeaseValidator() {
        super(Tenant.class);
    }

    @Override
    protected void init() {
        bind(proto().role(), new NotNullValidator());

        bind(proto().customer().person().name().firstName(), new NotNullValidator());
        bind(proto().customer().person().name().lastName(), new NotNullValidator());
        bind(proto().customer().person().sex(), new NotNullValidator());

        bind(proto().customer().person(), new HasPhoneValidator());
    }

}
