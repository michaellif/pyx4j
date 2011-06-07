/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 14, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.searchapt;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.propertyvista.portal.domain.dto.FloorplanDetailsDTO;

import com.pyx4j.widgets.client.style.IStyleSuffix;

public class UnitDetailsViewImpl extends DockPanel implements UnitDetailsView {

    private Presenter presenter;

    private final UnitDetailsForm unitForm;

    private final SlidesPanel slidesPanel;

    private final FlowPanel leftPanel;

    private final FlowPanel centerPanel;

    public static String DEFAULT_STYLE_PREFIX = "UnitDetailsViewImpl";

    public static enum StyleSuffix implements IStyleSuffix {
        Left, Center, PageHeader, Button
    }

    private static I18n i18n = I18nFactory.getI18n(UnitDetailsViewImpl.class);

    public UnitDetailsViewImpl() {

        unitForm = new UnitDetailsForm();
        unitForm.initialize();
        setStyleName(DEFAULT_STYLE_PREFIX);
        setSize("100%", "100%");
        slidesPanel = new SlidesPanel();

        VerticalPanel header = new VerticalPanel();
        header.setWidth("100%");

        Label pagetitle = new Label(i18n.tr("Floorplan Details"));
        pagetitle.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.PageHeader);
        header.add(pagetitle);
        add(header, DockPanel.NORTH);

        leftPanel = new FlowPanel();
        leftPanel.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Left);
        leftPanel.add(slidesPanel);
        add(leftPanel, DockPanel.WEST);

        Button inquire = new Button(i18n.tr("Apply"));
        inquire.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Button);
        inquire.setWidth("300px");
        inquire.setHeight("40px");
        leftPanel.add(inquire);

        centerPanel = new FlowPanel();
        centerPanel.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Center);
        add(centerPanel, DockPanel.CENTER);

        centerPanel.add(unitForm);

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        unitForm.setPresenter(presenter);
    }

    @Override
    public void populate(FloorplanDetailsDTO unit) {
        unitForm.populate(unit);
        slidesPanel.populate(unit);
    }
}
