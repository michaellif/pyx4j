/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-10
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.editors.CMoney;
import com.propertyvista.domain.financial.Money;

public class CrmEditorsComponentFactory extends VistaEditorsComponentFactory {

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member.getValueClass().equals(Money.class)) {
            return new CMoney();
        }
        return super.create(member);
    }
}
