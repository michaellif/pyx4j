/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-28
 * @author TPRGLET
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant;

import com.pyx4j.site.client.ui.crud.lister.IListerView;

import com.propertyvista.domain.tenant.income.PersonalAsset;
import com.propertyvista.domain.tenant.income.PersonalIncome;
import com.propertyvista.domain.tenant.income.TenantGuarantor;

public interface TenantScreeningView {

    interface Presenter {

        IListerView.Presenter getIncomePresenter();

        IListerView.Presenter getAssetPresenter();

        IListerView.Presenter getGuarantorPresenter();
    }

    IListerView<PersonalIncome> getIncomeListerView();

    IListerView<PersonalAsset> getAssetListerView();

    IListerView<TenantGuarantor> getGuarantorListerView();
}
