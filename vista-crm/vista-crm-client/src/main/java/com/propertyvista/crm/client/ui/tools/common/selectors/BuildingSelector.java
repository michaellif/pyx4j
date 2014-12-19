/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-06
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.tools.common.selectors;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.widgets.client.selector.SelectorListBox;

import com.propertyvista.crm.rpc.dto.selections.BuildingForSelectionDTO;

public class BuildingSelector extends SelectorListBox<BuildingForSelectionDTO> {

    public BuildingSelector() {
        super(new BuildingOptionsGrabber(), new IFormatter<BuildingForSelectionDTO, String>() {
            @Override
            public String format(BuildingForSelectionDTO value) {
                return SimpleMessageFormat.format("{0} - {1}", value.propertyCode(), value.name().getValue());
            }
        }, new BuildingSelectorOptionFormatter());
    }

}
