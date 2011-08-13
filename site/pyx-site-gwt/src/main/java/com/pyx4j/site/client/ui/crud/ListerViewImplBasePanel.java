/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Jun 26, 2011
 * @author Dad
 * @version $Id$
 */
package com.pyx4j.site.client.ui.crud;

import java.util.List;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;

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
    public void populate(List<E> entityes, int pageNumber, boolean hasMoreData) {
        assert (lister != null);
        lister.populate(entityes, pageNumber, hasMoreData);
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
