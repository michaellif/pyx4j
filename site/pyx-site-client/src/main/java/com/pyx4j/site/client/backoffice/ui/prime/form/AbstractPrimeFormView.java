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
 * Created on Jun 13, 2011
 * @author michaellif
 */
package com.pyx4j.site.client.backoffice.ui.prime.form;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.ui.prime.AbstractPrimePaneView;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView.IPrimeFormPresenter;
import com.pyx4j.widgets.client.HasSecureConcern;
import com.pyx4j.widgets.client.SecureConcernsHolder;

public abstract class AbstractPrimeFormView<E extends IEntity, PRESENTER extends IPrimeFormPresenter> extends AbstractPrimePaneView<PRESENTER> implements
        IPrimeFormView<E, PRESENTER> {

    private PrimeEntityForm<E> form;

    private PRESENTER presenter;

    private String captionBase;

    private final SecureConcernsHolder secureConcerns = new SecureConcernsHolder();

    public AbstractPrimeFormView() {
        super();
        secureConcerns.addAll(secureConcerns());
    }

    @Override
    public void setPresenter(PRESENTER presenter) {
        this.presenter = presenter;
        captionBase = (presenter != null && presenter.getPlace() != null ? AppSite.getHistoryMapper().getPlaceInfo(presenter.getPlace()).getCaption() : "");
    }

    @Override
    public PRESENTER getPresenter() {
        return presenter;
    }

    protected String getCaptionBase() {
        return captionBase;
    }

    protected void setCaptionBase(String captionBase) {
        this.captionBase = captionBase;
    }

    @Override
    protected final void setContentPane(IsWidget widget) {
        throw new Error("Call setForm instead of calling setContentPane");
    }

    /*
     * Should be called by descendant upon initialisation.
     */
    protected void setForm(PrimeEntityForm<E> form) {

        if (getContentPane() == null) { // finalise UI here:
            super.setContentPane(new LayoutPanel());
            setSize("100%", "100%");
        }

        if (this.form == form) {
            return; // already!?.
        }

        this.form = form;

        this.form.init();

        LayoutPanel center = (LayoutPanel) getContentPane();
        center.clear(); // remove current form...

        center.add(this.form);

    }

    protected PrimeEntityForm<E> getForm() {
        return form;
    }

    protected void addSecureConcern(HasSecureConcern secureConcern) {
        secureConcerns.addSecureConcern(secureConcern);
    }

    @Override
    public void populate(E value) {
        assert (form != null);
        form.populate(value);
        secureConcerns.setSecurityContext(value);
    }

    @Override
    public void reset() {
        setCaption(null);
        assert (form != null);
        form.reset();
        if (isVisorShown()) {
            hideVisor();
        }
        secureConcerns.setSecurityContext(null);
    }

    @Override
    public void setActiveTab(int index) {
        if (index >= 0) {
            assert (form != null);
            form.setActiveTab(index);
        }
    }

    @Override
    public int getActiveTab() {
        assert (form != null);
        return form.getActiveTab();
    }

}
