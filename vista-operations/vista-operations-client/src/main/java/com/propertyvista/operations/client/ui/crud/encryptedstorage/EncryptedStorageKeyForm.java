/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.encryptedstorage;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.operations.client.ui.crud.encryptedstorage.EncryptedStorageView.Presenter;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageKeyDTO;

public class EncryptedStorageKeyForm extends CEntityDecoratableForm<EncryptedStorageKeyDTO> {

    private static final I18n i18n = I18n.get(EncryptedStorageKeyForm.class);

    private Presenter presenter;

    private Button makeCurrent;

    private Button startKeyRotation;

    private Button enableDecrypt;

    public EncryptedStorageKeyForm() {
        super(EncryptedStorageKeyDTO.class);
        setViewable(true);
        setEditable(false);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel contentPanel = new FlowPanel();
        Widget statusPanel = makeStatusPanel();
        statusPanel.getElement().getStyle().setDisplay(Display.BLOCK);
        statusPanel.getElement().getStyle().setFloat(Style.Float.LEFT);
        statusPanel.getElement().getStyle().setWidth(40, Unit.EM);

        Widget controlPanel = makeControlPanel();
        controlPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        controlPanel.getElement().getStyle().setFloat(Style.Float.RIGHT);
        contentPanel.add(statusPanel);
        contentPanel.add(controlPanel);

        return contentPanel;
    }

    public void setPresenter(EncryptedStorageView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        makeCurrent.setEnabled(!getValue().isCurrent().isBooleanTrue());
        enableDecrypt.setEnabled(!getValue().decryptionEnabled().isBooleanTrue());
        startKeyRotation.setEnabled(true);
    }

    private Widget makeStatusPanel() {
        FormFlexPanel statusPanel = new FormFlexPanel();
        int row = -1;
        statusPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().isCurrent())).build());
        statusPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().decryptionEnabled())).build());
        statusPanel.setWidget(++row, 0, new HTML("&nbsp"));
        statusPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().recordsCount())).build());
        statusPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().created(), new CDatePicker())).build());
        statusPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().expired(), new CDatePicker())).build());
        return statusPanel;
    }

    private Widget makeControlPanel() {
        FlowPanel controlPanel = new FlowPanel();
        controlPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);

        makeCurrent = new Button(i18n.tr("SET AS CURRENT"), new Command() {
            @Override
            public void execute() {
                presenter.makeCurrentKey(EncryptedStorageKeyForm.this.getValue());
            }

        });
        controlPanel.add(new SimplePanel(makeCurrent));

        enableDecrypt = new Button(i18n.tr("Enable Decryption"), new Command() {
            @Override
            public void execute() {
                new PasswordEntryDialog() {
                    @Override
                    public boolean onClickOk() {
                        if (getPassword() != null) {
                            presenter.activateDecryption(EncryptedStorageKeyForm.this.getValue(), getPassword());
                            return true;
                        } else {
                            return false;
                        }
                    }
                };
            }

        });
        controlPanel.add(new SimplePanel(enableDecrypt));

        startKeyRotation = new Button(i18n.tr("Start Key Rotation"), new Command() {
            @Override
            public void execute() {
                presenter.startKeyRotation(EncryptedStorageKeyForm.this.getValue());
            }
        });
        controlPanel.add(new SimplePanel(startKeyRotation));

        return controlPanel;
    }
}
