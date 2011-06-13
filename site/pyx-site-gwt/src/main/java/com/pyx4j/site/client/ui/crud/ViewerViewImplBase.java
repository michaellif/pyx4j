/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-17
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.ui.crud;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.IEntity;

public class ViewerViewImplBase<E extends IEntity> extends DockLayoutPanel implements IViewerView<E> {

    private static I18n i18n = I18nFactory.getI18n(ViewerViewImplBase.class);

    private final ScrollPanel scroll = new ScrollPanel();

    protected CEntityForm<E> viewer = null;

    protected Presenter presenter;

    public ViewerViewImplBase() {
        super(Unit.EM);
    }

    public ViewerViewImplBase(Widget header, double size) {
        super(Unit.EM);
        addNorth(header, size);
        finalizeUi();
    }

    protected void finalizeUi() {
        add(scroll);
        setSize("100%", "100%");
    }

    /*
     * Should be called by descendant upon initialisation.
     */
    protected void setViewer(CEntityForm<E> viewer) {
        viewer.initialize();
        scroll.setWidget(this.viewer = viewer);
        this.viewer.asWidget().getElement().getStyle().setMargin(0.75, Unit.EM);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(E value) {
        assert (viewer != null);
        viewer.populate(value);
    }
}
