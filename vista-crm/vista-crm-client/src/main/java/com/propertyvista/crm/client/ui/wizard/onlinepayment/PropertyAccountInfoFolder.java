/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.wizard.onlinepayment;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.dto.vista2pmc.OnlinePaymentSetupDTO;
import com.propertyvista.dto.vista2pmc.OnlinePaymentSetupDTO.PropertyAccountInfo;

public class PropertyAccountInfoFolder extends VistaBoxFolder<OnlinePaymentSetupDTO.PropertyAccountInfo> {

    public PropertyAccountInfoFolder() {
        super(PropertyAccountInfo.class);
    }

    @Override
    public <T extends CComponent<T, ?>> T create(IObject<?> member) {
        if (member instanceof PropertyAccountInfo) {
            return (T) new PropertyAccountInfoForm();
        }
        return super.create(member);
    }

}