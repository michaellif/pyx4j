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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.selectors;

import java.text.ParseException;

import com.pyx4j.commons.IFormat;

import com.propertyvista.crm.client.ui.tools.common.widgets.superselector.SuperSuggestiveSelector;
import com.propertyvista.crm.rpc.dto.selections.BuildingForSelectionDTO;

public class BuildingSelector extends SuperSuggestiveSelector<BuildingForSelectionDTO> {

    public BuildingSelector() {
        super(new IFormat<BuildingForSelectionDTO>() {

            @Override
            public String format(BuildingForSelectionDTO value) {
                return value.propertyCode().getValue() + " (" + value.name().getValue() + ")";
            }

            @Override
            public BuildingForSelectionDTO parse(String string) throws ParseException {
                return null;
            }

        }, new BuildingForSelectionCell(), new BuildingSuggestionsProvider(), true);
    }

}
