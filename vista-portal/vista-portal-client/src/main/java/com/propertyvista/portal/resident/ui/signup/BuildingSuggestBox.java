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

import java.util.Collection;
import java.util.Comparator;

import com.pyx4j.forms.client.ui.CSelectorTextBox;
import com.pyx4j.widgets.client.suggest.MultyWordSuggestOptionsGrabber;

import com.propertyvista.portal.rpc.portal.resident.dto.SelfRegistrationBuildingDTO;

public class BuildingSuggestBox extends CSelectorTextBox<SelfRegistrationBuildingDTO> {

    private MultyWordSuggestOptionsGrabber<SelfRegistrationBuildingDTO> optionsGrabber;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public BuildingSuggestBox() {
        super(new MultyWordSuggestOptionsGrabber<SelfRegistrationBuildingDTO>());

        optionsGrabber = ((MultyWordSuggestOptionsGrabber) getOptionsGrabber());
        optionsGrabber.setFormatter(getFormatter());
        optionsGrabber.setComparator(new Comparator<SelfRegistrationBuildingDTO>() {
            @Override
            public int compare(SelfRegistrationBuildingDTO paramT1, SelfRegistrationBuildingDTO paramT2) {
                return paramT1.getStringView().compareTo(paramT2.getStringView());
            }
        });
    }

    public void setOptions(Collection<SelfRegistrationBuildingDTO> options) {
        optionsGrabber.setAllOptions(options);
    }

    @Override
    public boolean isValueEmpty() {
        return (getValue() == null) || getValue().isNull() || getValue().buildingKey().isNull();
    }
}
