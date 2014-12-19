/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-08
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.crud.administration.profile.paymentmethods;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.rpc.dto.admin.PmcPaymentMethodsDTO;

public class PmcPaymentMethodsEditorViewImpl extends CrmEditorViewImplBase<PmcPaymentMethodsDTO> implements PmcPaymentMethodsEditorView {

    public PmcPaymentMethodsEditorViewImpl() {
        setForm(new PmcPaymentMethodsForm(this));
    }

}
