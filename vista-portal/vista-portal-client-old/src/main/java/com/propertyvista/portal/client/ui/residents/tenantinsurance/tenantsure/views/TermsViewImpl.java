/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-06
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class TermsViewImpl extends Composite implements TermsView {

    private final Label terms;

    public TermsViewImpl() {
        terms = new Label();
        terms.setSize("100%", "100%");

        ScrollPanel scrollPanel = new ScrollPanel(terms);
        scrollPanel.getElement().getStyle().setPadding(20, Unit.PX);

        initWidget(scrollPanel);
    }

    @Override
    public void populate(String tenantSureFaqHtml) {
        terms.getElement().setInnerHTML(tenantSureFaqHtml);
    }

}
