/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 10, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.web.dto.financial;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.financial.billing.InvoicePayment;

@Transient
public interface LatestActivitiesDTO extends IEntity {

    IList<InvoicePayment> lineItems();
}
