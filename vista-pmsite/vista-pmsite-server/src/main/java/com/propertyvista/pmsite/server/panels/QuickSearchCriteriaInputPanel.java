/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 24, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class QuickSearchCriteriaInputPanel extends Panel {

    private static final long serialVersionUID = 1L;

    public QuickSearchCriteriaInputPanel(String id, IModel<QuickSearchModel> model) {
        super(id, model);
    }

}
