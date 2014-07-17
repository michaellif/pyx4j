/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-26
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.signup;

import com.pyx4j.forms.client.ui.selector.CSelectorBox;

import com.propertyvista.portal.rpc.portal.resident.dto.SelfRegistrationBuildingDTO;

public class BuildingSuggestBox extends CSelectorBox<SelfRegistrationBuildingDTO> {

    public BuildingSuggestBox() {
    }

    @Override
    public boolean isValueEmpty() {
        return (getValue() == null) || getValue().isNull() || getValue().buildingKey().isNull();
    }
}
