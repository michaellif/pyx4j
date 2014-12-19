/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2011
 * @author Dad
 */
package com.propertyvista.portal.resident.ui.services.insurance;

import com.propertyvista.portal.rpc.portal.resident.dto.insurance.GeneralInsurancePolicyDTO;
import com.propertyvista.portal.shared.ui.AbstractEditorView;

public class GeneralPolicyPageViewImpl extends AbstractEditorView<GeneralInsurancePolicyDTO> implements GeneralPolicyPageView {

    public GeneralPolicyPageViewImpl() {
        setForm(new GeneralPolicyPage(this) {
            @Override
            protected void onRemove() {
                ((GeneralPolicyPageView.GeneralPolicyPagePresenter) getPresenter()).remove();
            }
        });
    }
}
