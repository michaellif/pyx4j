/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.selectors;

import java.text.ParseException;

import com.pyx4j.forms.client.ui.IFormat;

import com.propertyvista.crm.client.ui.tools.common.widgets.superselector.SuperSuggestiveSelector;
import com.propertyvista.crm.rpc.dto.selections.PortfolioForSelectionDTO;

public class PortfolioSelector extends SuperSuggestiveSelector<PortfolioForSelectionDTO> {

    public PortfolioSelector() {
        super(new IFormat<PortfolioForSelectionDTO>() {
            @Override
            public String format(PortfolioForSelectionDTO value) {
                return value.name().getValue();
            }

            @Override
            public PortfolioForSelectionDTO parse(String string) throws ParseException {
                return null;
            }
        }, new PortfolioForSelectionCell(), new PortfolioSuggestionsProvider(), true);
    }

}
