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

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.ui.prime.AbstractPrimePaneView;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView.IPrimeFormPresenter;
import com.pyx4j.site.client.ui.layout.AbstractSimpleLayoutPanel;
import com.pyx4j.site.client.ui.layout.LayoutSystem;
import com.pyx4j.widgets.client.HasSecureConcern;
import com.pyx4j.widgets.client.SecureConcernsHolder;

public abstract class AbstractPrimeFormView<E extends IEntity, PRESENTER extends IPrimeFormPresenter> extends AbstractPrimePaneView<PRESENTER>
        implements IPrimeFormView<E, PRESENTER> {

    private PrimeEntityForm<E> form;

    private String captionBase;

    private final SecureConcernsHolder secureConcerns = new SecureConcernsHolder();

    public AbstractPrimeFormView(LayoutSystem layoutSystem) {
        super(layoutSystem);
        secureConcerns.addAll(secureConcerns());
    }

    @Override
    public void setPresenter(PRESENTER presenter) {
        super.setPresenter(presenter);
        if (presenter != null && presenter.getPlace() != null) {
            captionBase = AppSite.getHistoryMapper().getPlaceInfo(presenter.getPlace()).getCaption() + ": ";
        } else {
            captionBase = "";
        }
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
     * Should be called by descendant upon initialization.
     */
    protected void setForm(PrimeEntityForm<E> form) {

        if (getContentPane() == null) { // finalize UI here:
            super.setContentPane(getLayoutSystem().createSimpleLayoutPanel());
            setSize("100%", "100%");
        }

        if (this.form == form) {
            return; // already!?.
        }

        this.form = form;
        this.form.init();

        ((AbstractSimpleLayoutPanel) getContentPane()).setWidget(this.form);
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
        onPopulate();
    }

    /**
     * Called after data is shown/propagated to UI components
     */
    protected void onPopulate() {
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
