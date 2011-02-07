package com.propertyvista.portal.tester.ui;

import java.util.Map;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.CTextField;

public class SignUpResultViewImpl extends VerticalPanel implements SignUpResultView {

    private static I18n i18n = I18nFactory.getI18n(SignUpResultViewImpl.class);

    private Presenter presenter;

    public SignUpResultViewImpl() {

        //TODO: debug IDS
        Map<String, String> params = presenter.getParams();

        CTextField username = new CTextField("Username");
        username.setEditable(false);
        username.setEnabled(false);
        username.setValue(params.get(username.getTitle()));

        CPasswordTextField password = new CPasswordTextField("Password");
        password.setEditable(false);
        password.setEnabled(false);
        password.setValue(params.get(password.getTitle()).replaceAll(".", "*"));

        CComboBox<String> accountType = new CComboBox<String>("Account type");
        accountType.setEditable(false);
        accountType.setEnabled(false);
        accountType.setValue(params.get(accountType.getTitle()));

        CCheckBox internalCustomer = new CCheckBox("Internal customer");
        internalCustomer.setEditable(false);
        internalCustomer.setEnabled(false);
        internalCustomer.setValue(Boolean.parseBoolean(params.get(internalCustomer.getTitle())));

        CComponent<?>[][] components = new CComponent[][] {

        { username },

        { password },

        { accountType },

        { internalCustomer },

        };

        Widget form = CForm.createDecoratedFormWidget(LabelAlignment.TOP, components, i18n.tr("Sign-up"));
        add(form);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

}
