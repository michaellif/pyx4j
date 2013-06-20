/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 1, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.field.client.ui.components.search;

import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.forms.client.ui.CTextArea;

public class SearchResultsViewImpl extends FlowPanel implements SearchResultsView {

    private final CTextArea searchPanel;

    public SearchResultsViewImpl() {
        setSize("100%", "100%");

        searchPanel = new CTextArea();
        searchPanel.setHeight("100%");
        searchPanel.setWidth("70%");
        searchPanel.setTitle("Search Results");

        add(searchPanel);
    }

    @Override
    public void populateResults(String result) {
        searchPanel.setValue(result);
    }

}
