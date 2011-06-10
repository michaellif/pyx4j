/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-18
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud;

import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;

public class ListerViewImplBase<E extends IEntity> extends DockLayoutPanel implements IListerView<E> {

    private static I18n i18n = I18nFactory.getI18n(ListerViewImplBase.class);

    private final ScrollPanel scroll = new ScrollPanel();

    protected ListerBase<E> lister = null;

    public ListerViewImplBase(AppPlace place) {
        super(Unit.EM);
        setSize("100%", "100%");
        addNorth(new CrmHeaderDecorator(AppSite.getHistoryMapper().getPlaceInfo(place).getCaption()), 3);
        add(scroll);
    }

    /*
     * Should be called by descendant upon initialisation.
     */
    protected void setLister(ListerBase<E> lister) {
        scroll.setWidget(this.lister = lister);
    }

    protected ListerBase<E> getLister() {
        return lister;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        assert (lister != null);
        lister.setPresenter(presenter);
    }

    @Override
    public int getPageSize() {
        assert (lister != null);
        return lister.getPageSize();
    }

    @Override
    public void populateData(List<E> entityes, int pageNumber, boolean hasMoreData) {
        assert (lister != null);
        lister.populateData(entityes, pageNumber, hasMoreData);
    }
}
