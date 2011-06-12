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
package com.propertyvista.crm.client.ui.crud;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.crm.rpc.CrudAppPlace;

public class ViewerViewImplBase<E extends IEntity> extends DockLayoutPanel implements IViewerView<E> {

    private static I18n i18n = I18nFactory.getI18n(ViewerViewImplBase.class);

    private final ScrollPanel scroll = new ScrollPanel();

    protected CEntityForm<E> viewer = null;

    protected Presenter presenter;

    protected final Class<? extends CrudAppPlace> placeClass;

    protected final CrmHeaderDecorator header;

    protected final String defaultCaption;

    public ViewerViewImplBase(Class<? extends CrudAppPlace> placeClass) {
        super(Unit.EM);
        this.placeClass = placeClass;
        setSize("100%", "100%");
        defaultCaption = AppSite.getHistoryMapper().getPlaceInfo(placeClass).getCaption();
        addNorth(header = new CrmHeaderDecorator(defaultCaption, createActionsPanel()), 3);
        add(scroll);
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
        header.setCaption(defaultCaption + " " + value.getStringView());
    }

    private Widget createActionsPanel() {
        HorizontalPanel buttons = new HorizontalPanel();
        AnchorButton btnEdit = new AnchorButton(i18n.tr("Edit"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.edit(placeClass);
            }
        });
        btnEdit.addStyleName(btnEdit.getStylePrimaryName() + VistaCrmTheme.StyleSuffixEx.EditButton);
        buttons.add(btnEdit);
        return buttons;
    }
}
