/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.viewfactories;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.crm.client.ui.wizard.creditcheck.CreditCheckWizardView;
import com.propertyvista.crm.client.ui.wizard.creditcheck.CreditCheckWizardViewImpl;
import com.propertyvista.crm.client.ui.wizard.onlinepayment.OnlinePaymentWizardView;
import com.propertyvista.crm.client.ui.wizard.onlinepayment.OnlinePaymentWizardViewImpl;

public class WizardViewFactory extends ViewFactoryBase {

    public static <E extends IEntity, T extends IPane> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (CreditCheckWizardView.class.equals(type)) {
                map.put(type, new CreditCheckWizardViewImpl());
            } else if (OnlinePaymentWizardView.class.equals(type)) {
                map.put(type, new OnlinePaymentWizardViewImpl());
            }
        }
        @SuppressWarnings("unchecked")
        T impl = (T) map.get(type);
        if (impl == null) {
            throw new Error("implementation of " + type.getName() + " not found");
        }
        return impl;
    }
}
