/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui;

import java.util.List;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.site.client.backoffice.ui.prime.lister.EntityDataTablePanel;
import com.pyx4j.site.client.backoffice.ui.prime.lister.IListerView;
import com.pyx4j.site.client.ui.visor.IVisor;

public class ListerViewImplBasePanel<E extends IEntity> extends DockPanel implements IListerView<E> {

    protected EntityDataTablePanel<E> lister = null;

    private Widget header = null;

    public ListerViewImplBasePanel() {
        super();
        setSize("100%", "100%");
    }

    public ListerViewImplBasePanel(EntityDataTablePanel<E> lister) {
        this();
        setLister(lister);
    }

    public ListerViewImplBasePanel(Widget header) {
        super();
        setHeader(header);
    }

    protected void setHeader(Widget header) {
        if (this.header != null) {
            this.header.removeFromParent();
        }
        this.header = header;
        add(header, DockPanel.NORTH);
    }

    /*
     * Should be called by descendant upon initialization.
     */
    protected void setLister(EntityDataTablePanel<E> lister) {
        if (this.lister != null) {
            this.lister.removeFromParent();
        }
        add(this.lister = lister, DockPanel.CENTER);
    }

    @Override
    public EntityDataTablePanel<E> getDataTablePanel() {
        assert (lister != null);
        return lister;
    }

    @Override
    public void setPresenter(IListerPresenter<E> presenter) {
        getDataTablePanel().setPresenter(presenter);
    }

    @Override
    public IListerPresenter<E> getPresenter() {
        return getDataTablePanel().getPresenter();
    }

    @Override
    public int getPageSize() {
        return getDataTablePanel().getPageSize();
    }

    @Override
    public int getPageNumber() {
        return getDataTablePanel().getPageNumber();
    }

    @Override
    public void onDeleted(Key itemID, boolean isSuccessful) {
        // TODO Auto-generated method stub
    }

    @Override
    public List<Criterion> getFilters() {
        return getDataTablePanel().getFilters();
    }

    @Override
    public void setFilters(List<Criterion> filterData) {
        getDataTablePanel().setFilters(filterData);
    }

    @Override
    public List<Sort> getSortCriteria() {
        return getDataTablePanel().getSortCriteria();
    }

    @Override
    public void setSortCriteria(List<Sort> sorts) {
        getDataTablePanel().setSortCriteria(sorts);
    }

    @Override
    public void discard() {
        getDataTablePanel().discard();
    }

    @Override
    public void showVisor(IVisor visor) {
        // TODO Auto-generated method stub

    }

    @Override
    public void hideVisor() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isVisorShown() {
        // TODO Auto-generated method stub
        return false;
    }
}
