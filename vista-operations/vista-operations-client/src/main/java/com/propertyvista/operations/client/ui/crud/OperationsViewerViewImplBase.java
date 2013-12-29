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
package com.propertyvista.operations.client.ui.crud;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IVersionData;
import com.pyx4j.entity.core.IVersionedEntity;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.rpc.AbstractVersionDataListService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.AbstractViewer;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.versioning.VersionSelectorDialog;

public class OperationsViewerViewImplBase<E extends IEntity> extends AbstractViewer<E> {

    private static final I18n i18n = I18n.get(OperationsViewerViewImplBase.class);

    private Button editButton;

    private Button selectVersion;

    private Button finalizeButton;

    public OperationsViewerViewImplBase() {
        this(false);
    }

    public OperationsViewerViewImplBase(boolean viewOnly) {

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
