/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.editors;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;

public class EditorViewImplBase<E extends IEntity> extends DockLayoutPanel implements IEditorView<E> {

    private static I18n i18n = I18nFactory.getI18n(EditorViewImplBase.class);

    private final ScrollPanel scroll = new ScrollPanel();

    protected CEntityForm<E> editor = null;

    protected Presenter presenter;

    public EditorViewImplBase(AppPlace place) {
        super(Unit.EM);
        setSize("100%", "100%");
        addNorth(new CrmHeaderDecorator(AppSite.getHistoryMapper().getPlaceInfo(place).getCaption()), 3);
        addSouth(createButtons(), 4);
        add(scroll);
    }

    /*
     * Should be called by descendant upon initialisation.
     */
    protected void setEditor(CEntityForm<E> editor) {
        editor.initialize();
        scroll.setWidget(this.editor = editor);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(E value) {
        assert (editor != null);
        editor.populate(value);
    }

    @Override
    public E getValue() {
        return editor.getValue();
    }

    private Widget createButtons() {
        HorizontalPanel buttons = new HorizontalPanel();
        buttons.add(new Button("Save", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                editor.setVisited(true);
                if (!editor.isValid()) {
                    throw new UserRuntimeException(editor.getValidationResults().getMessagesText(true));
                }

                presenter.save();
            }
        }));
        buttons.add(new Button("Cancel", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.cancel();
            }
        }));
        buttons.setSpacing(10);
        SimplePanel wrap = new SimplePanel();
        wrap.getElement().getStyle().setProperty("borderTop", "1px solid #bbb");
        wrap.setWidget(buttons);
        wrap.setWidth("100%");
        return wrap;
    }
}
