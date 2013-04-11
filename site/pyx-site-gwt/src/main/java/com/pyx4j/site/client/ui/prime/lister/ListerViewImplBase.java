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
package com.pyx4j.site.client.ui.prime.lister;

import java.util.List;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.AbstractPane;
import com.pyx4j.site.client.ui.prime.misc.IMemento;

public class ListerViewImplBase<E extends IEntity> extends AbstractPane implements ILister<E> {

    protected AbstractLister<E> lister = null;

    public ListerViewImplBase() {
        super();
    }

    /*
     * Should be called by descendant upon initialisation.
     */
    protected void setLister(AbstractLister<E> lister) {
        if (getContentPane() == null) { // finalise UI here:
            setContentPane(new ScrollPanel());
            setSize("100%", "100%");
        }

        if (this.lister == lister) {
            return; // already!?.
        }

        ((ScrollPanel) getContentPane()).setWidget(this.lister = lister);
    }

    @Override
    public AbstractLister<E> getLister() {
        assert (lister != null);
        return lister;
    }

    @Override
    public void setPresenter(ILister.Presenter<E> presenter) {
        getLister().setPresenter(presenter);
        setCaption(presenter != null && presenter.getPlace() != null ? AppSite.getHistoryMapper().getPlaceInfo(presenter.getPlace()).getCaption() : "");
    }

    @Override
    public ILister.Presenter<E> getPresenter() {
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
    public List<Criterion> getFilters() {
        return getLister().getFilters();
    }

    @Override
    public void setFilters(List<Criterion> filterData) {
        getLister().setFilters(filterData);
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

    @Override
    public void onDeleted(Key itemID, boolean isSuccessful) {
        // TODO Auto-generated method stub
    }

    @Override
    public void discard() {
        getLister().discard();
    }
}
