/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 8, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

public class FormErrorPanel extends FeedbackPanel {
    private static final long serialVersionUID = 1L;

    private final List<String> idList;

    public FormErrorPanel(String id, String... compIds) {
        super(id);
        idList = Arrays.asList(compIds);
        setFilter(new IFeedbackMessageFilter() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean accept(FeedbackMessage message) {
                return idList.contains(message.getReporter().getId());
            }
        });
        // set the style
        Component ul = get("feedbackul");
        if (ul != null) {
            ul.add(AttributeModifier.replace("class", "errorPanel"));
        }
    }
}
