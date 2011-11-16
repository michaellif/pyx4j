/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Created on 2011-05-18
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.ui.crud.lister;

import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.DefaultSiteCrudPanelsTheme;
import com.pyx4j.site.client.ui.crud.misc.IMemento;
import com.pyx4j.widgets.client.actionbar.Toolbar;

public class ListerViewImplBase<E extends IEntity> extends DockLayoutPanel implements IListerView<E> {

    protected static I18n i18n = I18n.get(ListerViewImplBase.class);

    protected ListerBase<E> lister = null;

    private final Toolbar toolbar;

    private final Widget header;

    public ListerViewImplBase(Widget header, double size) {
        super(Unit.EM);
        this.header = header;
        addNorth(header, size);

        SimplePanel actionsBar = new SimplePanel();
        actionsBar.setStyleName(DefaultSiteCrudPanelsTheme.StyleName.ActionsPanel.name());

        toolbar = new Toolbar();
        actionsBar.setWidget(toolbar);
        addNorth(actionsBar, 3);

    }

    public Widget getHeader() {
        return header;
    }

    /*
     * Should be called by descendant upon initialisation.
     */
    protected void setLister(ListerBase<E> lister) {
        if (getCenter() == null) { // finalise UI here:
            add(new ScrollPanel());
            setSize("100%", "100%");
        }

        if (this.lister == lister) {
            return; // already!?.
        }

        ((ScrollPanel) getCenter()).setWidget(this.lister = lister);
    }

    @Override
    public ListerBase<E> getLister() {
        assert (lister != null);
        return lister;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        getLister().setPresenter(presenter);
    }

    @Override
    public Presenter getPresenter() {
        return getLister().getPresenter();
    }

    @Override
    public int getPageSize() {
        return getLister().getPageSize();
    }

    @Override
    public int getPageNumber() {
        return getLister().getPageNumber();
    }

    @Override
    public void populate(List<E> entityes, int pageNumber, boolean hasMoreData, int totalRows) {
        getLister().populate(entityes, pageNumber, hasMoreData, totalRows);
    }

    @Override
    public List<DataTableFilterData> getFiltering() {
        return getLister().getFiltering();
    }

    @Override
    public void setFiltering(List<DataTableFilterData> filterData) {
        getLister().setFiltering(filterData);
    }

    @Override
    public List<Sort> getSorting() {
        return getLister().getSorting();
    }

    @Override
    public void setSorting(List<Sort> sorts) {
        getLister().setSorting(sorts);
    }

    @Override
    public IMemento getMemento() {
        return getLister().getMemento();
    }

    @Override
    public void storeState(Place place) {
        getLister().storeState(place);
    }

    @Override
    public void restoreState() {
        getLister().restoreState();
    }

    public void addActionButton(Widget widget) {
        toolbar.addItem(widget, true);
    }
}
