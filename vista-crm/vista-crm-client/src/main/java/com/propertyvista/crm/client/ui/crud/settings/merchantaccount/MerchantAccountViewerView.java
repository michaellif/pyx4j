/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 12, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.merchantaccount;

import com.pyx4j.site.client.ui.prime.form.IViewer;
import com.pyx4j.site.client.ui.prime.lister.ILister;

import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.property.asset.building.Building;

public interface MerchantAccountViewerView extends IViewer<MerchantAccount> {

    ILister<Building> getBuildingListerView();
}
