/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-05
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.validation.validators.lead;

import com.propertyvista.biz.validation.framework.validators.CompositeEntityValidator;
import com.propertyvista.biz.validation.framework.validators.NotNullValidator;
import com.propertyvista.domain.tenant.lead.Lead;

public class LeadValidator extends CompositeEntityValidator<Lead> {

    public LeadValidator() {
        super(Lead.class);
    }

    @Override
    protected void init() {
        bind(proto().leaseType(), new NotNullValidator());
        bind(proto().leaseTerm(), new NotNullValidator());
        bind(proto().moveInDate(), new NotNullValidator());
        bind(proto().floorplan(), new NotNullValidator());
    }
}
