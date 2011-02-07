package com.propertyvista.portal.tester.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.CTextField;

public class SignUpViewImpl extends VerticalPanel implements SignUpView {

    private static I18n i18n = I18nFactory.getI18n(SignUpViewImpl.class);

    private Presenter presenter;

    public SignUpViewImpl() {

        //TODO: debug IDS

        final CTextField username = new CTextField("Username");
        final CPasswordTextField password = new CPasswordTextField("Password");
        final CPasswordTextField passwordVerify = new CPasswordTextField("Verify password");
        final CComboBox<String> accountType = new CComboBox<String>("Account type");
        accountType.setOptions(Arrays.asList(new String[] { "Corporate", "Personal" }));
        final CCheckBox internalCustomer = new CCheckBox("Internal customer");

        CHyperlink terms = new CHyperlink(null, new Command() {

            @Override
            public void execute() {
                // TODO Auto-generated method stub

            }
        });
        terms.setValue(i18n.tr("Terms and conditions"));

        CComponent<?>[][] components = new CComponent[][] {

        { username },

        { password },

        { passwordVerify },

        { accountType },

        { internalCustomer },

        { terms },

        };

        Widget form = CForm.createDecoratedFormWidget(LabelAlignment.TOP, components, i18n.tr("Sign-up"));
        add(form);

        Button signUpButton = new Button(i18n.tr("Sign-up"));
        signUpButton.ensureDebugId(CrudDebugId.Criteria_Submit.toString());
        signUpButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Map<String, String> params = new HashMap<String, String>();
                params.put(username.getTitle(), username.getValue());
                params.put(password.getTitle(), username.getValue());
                params.put(accountType.getTitle(), accountType.getValue());
                params.put(internalCustomer.getTitle(), internalCustomer.getValue() + "");
                presenter.goToSignUpResult(params);
            }

        });
        signUpButton.getElement().getStyle().setProperty("margin", "3px 20px 3px 8px");
        add(signUpButton);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

}
