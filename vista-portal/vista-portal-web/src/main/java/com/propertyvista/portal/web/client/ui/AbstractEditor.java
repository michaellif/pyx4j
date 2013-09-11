/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.i18n.shared.I18n;

public class AbstractEditor<E extends IEntity> extends AbstractPortalPanel implements IEntityEditorView<E> {

    protected static final I18n i18n = I18n.get(AbstractEditor.class);

    private EntityPresenter<E> presenter;

    private CEntityForm<E> form;

    public AbstractEditor() {
        super();
    }

    protected void setForm(final CEntityForm<E> form) {
        this.form = form;
        if (form == null) {
            setWidget(null);
        } else {
            form.initContent();
            setWidget(form.asWidget());
        }
    }

    @Override
    public void setPresenter(EntityPresenter<E> presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(E value) {
        form.reset();
        form.populate(value);
    }

    public EntityPresenter<E> getPresenter() {
        return presenter;
    }

    public CEntityForm<E> getForm() {
        return form;
    }

}
