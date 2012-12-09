/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.wizard.creditcheck;

import com.propertyvista.crm.client.ui.crud.CrmWizardViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.BuildingDTO;

public class CreditCheckWizardViewImpl extends CrmWizardViewImplBase<BuildingDTO> implements CreditCheckWizardView {

    public CreditCheckWizardViewImpl() {
        super(CrmSiteMap.Properties.Building.class, new CreditCheckWizardForm());
    }
}