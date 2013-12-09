/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 18, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.selections;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;

import com.propertyvista.crm.rpc.dto.selections.PortfolioForSelectionDTO;
import com.propertyvista.domain.company.Portfolio;

public interface SelectPortfolioListService extends AbstractListService<Portfolio> {

    void getPortfoliosForSelection(AsyncCallback<Vector<PortfolioForSelectionDTO>> callback, EntityListCriteria<Portfolio> criteria);

}
