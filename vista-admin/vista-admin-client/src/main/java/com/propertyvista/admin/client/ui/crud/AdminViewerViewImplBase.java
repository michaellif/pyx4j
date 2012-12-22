/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-17
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.rpc.AbstractVersionDataListService;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IVersionData;
import com.pyx4j.entity.shared.IVersionedEntity;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.form.ViewerViewImplBase;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.versioning.VersionSelectorDialog;

public class AdminViewerViewImplBase<E extends IEntity> extends ViewerViewImplBase<E> {

    private static final I18n i18n = I18n.get(AdminViewerViewImplBase.class);

    protected final String defaultCaption;

    private Button editButton;

    private Button selectVersion;

    private Button finalizeButton;

    public AdminViewerViewImplBase(Class<? extends CrudAppPlace> placeClass) {
        this(placeClass, false);
    }

    public AdminViewerViewImplBase(Class<? extends CrudAppPlace> placeClass, boolean viewOnly) {
        super();

        defaultCaption = (placeClass != null ? AppSite.getHistoryMapper().getPlaceInfo(placeClass).getCaption() : "");
        setCaption(defaultCaption);

        if (!viewOnly) {
            editButton = new Button(i18n.tr("Edit"), new Command() {
                @Override
                public void execute() {
                    getPresenter().edit();
                }
            });

            addHeaderToolbarItem(editButton);
        }
    }

    public AdminViewerViewImplBase(Class<? extends CrudAppPlace> placeClass, AdminEntityForm<E> form) {
        this(placeClass);
        setForm(form);
    }

    public AdminViewerViewImplBase(Class<? extends CrudAppPlace> placeClass, AdminEntityForm<E> form, boolean viewOnly) {
        this(placeClass, viewOnly);
        setForm(form);
    }

    public Button getEditButton() {
        return editButton;
    }

    public Button getSelectVersion() {
        return selectVersion;
    }

    public Button getFinalizeButton() {
        return finalizeButton;
    }

    @Override
    public void reset() {
        if (finalizeButton != null) {
            finalizeButton.setVisible(false);
        }
        super.reset();
    }

    @Override
    public void populate(E value) {
        super.populate(value);

        String caption = (defaultCaption + " " + value.getStringView());
        if (selectVersion != null) {
            if (((IVersionedEntity<?>) value).version().versionNumber().isNull()) {
                caption = caption + " (" + ((IVersionedEntity<?>) value).version().versionNumber().getStringView() + ")";
            } else {
                caption = caption + ", " + i18n.tr("version") + " #" + ((IVersionedEntity<?>) value).version().versionNumber().getStringView() + " ("
                        + ((IVersionedEntity<?>) value).version().fromDate().getStringView() + ")";
            }
        }
        setCaption(caption);

        if (editButton != null) {
            editButton.setEnabled(super.getPresenter().canEdit());
        }

        if (finalizeButton != null) {
            finalizeButton.setVisible(((IVersionedEntity<?>) value).version().versionNumber().isNull());
        }
    }

    protected <V extends IVersionData<?>> void enableVersioning(final Class<V> entityVersionClass, final AbstractVersionDataListService<V> entityVersionService) {

        selectVersion = new Button(i18n.tr("Select Version"), new Command() {
            @Override
            public void execute() {
                new VersionSelectorDialog<V>(entityVersionClass, getForm().getValue().getPrimaryKey()) {
                    @Override
                    public boolean onClickOk() {
                        getPresenter().view(getSelectedVersionId());
                        return true;
                    }

                    @Override
                    protected AbstractListService<V> getSelectService() {
                        return entityVersionService;
                    }
                }.show();
            }
        });
        addHeaderToolbarItem(selectVersion.asWidget());

        finalizeButton = new Button(i18n.tr("Finalize"), new Command() {
            @Override
            public void execute() {
                getPresenter().approveFinal();
            }
        });
        addHeaderToolbarItem(finalizeButton.asWidget());

        if (editButton != null) {
            editButton.setCaption(i18n.tr("Edit Draft"));
        }
    }
}
