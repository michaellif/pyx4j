/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 1, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Versioned;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;

import com.propertyvista.domain.financial.offering.Concession;

@EmbeddedEntity
public interface LeaseProducts extends IEntity {

    @Owned
    @NotNull
    BillableItem serviceItem();

    @Owned
    @Caption(name = "Features")
    IList<BillableItem> featureItems();

    @Versioned
    IList<Concession> concessions();

}
