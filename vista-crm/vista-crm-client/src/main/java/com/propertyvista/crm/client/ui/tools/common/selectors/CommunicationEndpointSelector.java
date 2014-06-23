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

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.widgets.client.selector.SuggestiveSelector;

import com.propertyvista.dto.CommunicationEndpointDTO;

public class CommunicationEndpointSelector extends SuggestiveSelector<CommunicationEndpointDTO> {

    public CommunicationEndpointSelector() {
        super(new IFormatter<CommunicationEndpointDTO, String>() {
            @Override
            public String format(CommunicationEndpointDTO value) {
                return value.name().getValue();
            }
        }, new IParser<CommunicationEndpointDTO>() {
            @Override
            public CommunicationEndpointDTO parse(String string) throws ParseException {
                return null;
            }
        }, new CommunicationEndpointForSelectionCell(), new CommunicationEndpointSuggestionsProvider(), true, false);
    }

}
