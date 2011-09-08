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
package com.propertyvista.portal.client.ui;

import java.util.List;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.site.client.ui.crud.FilterData;
import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.ListerBase;

public class ListerViewImplBasePanel<E extends IEntity> extends DockPanel implements IListerView<E> {

    protected ListerBase<E> lister = null;

    private Widget header = null;

    public ListerViewImplBasePanel() {
        super();
        setSize("100%", "100%");
    }

    public ListerViewImplBasePanel(ListerBase<E> lister) {
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
    protected void setLister(ListerBase<E> lister) {
        if (this.lister != null) {
            this.lister.removeFromParent();
        }
        add(this.lister = lister, DockPanel.CENTER);
    }

    @Override
    public ListerBase<E> getLister() {
        return lister;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        assert (lister != null);
        lister.setPresenter(presenter);
    }

    @Override
    public Presenter getPresenter() {
        assert (lister != null);
        return lister.getPresenter();
    }

    @Override
    public int getPageSize() {
        assert (lister != null);
        return lister.getPageSize();
    }

    @Override
    public int getPageNumber() {
        assert (lister != null);
        return lister.getPageNumber();
    }

    @Override
    public void populate(List<E> entityes, int pageNumber, boolean hasMoreData, int totalRows) {
        assert (lister != null);
        lister.populate(entityes, pageNumber, hasMoreData, totalRows);
    }

    @Override
    public List<FilterData> getFiltering() {
        return lister.getFiltering();
    }

    @Override
    public List<Sort> getSorting() {
        return lister.getSorting();
    }
}
