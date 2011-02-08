package com.propertyvista.portal.tester.ui;

import java.util.Map;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.StringDebugId;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.forms.client.ui.CLabel;

public class SignUpResultViewImpl extends VerticalPanel implements SignUpResultView {

    private static I18n i18n = I18nFactory.getI18n(SignUpResultViewImpl.class);

    private Presenter presenter;

    public SignUpResultViewImpl() {

        Map<String, String> params = presenter.getParams();

        //TODO: make sure i18n parses SignUpView.USERNAME_TITLE

        CLabel username = new CLabel(SignUpView.USERNAME_TITLE);
        username.setDebugId(SignUpView.FIELDS.userName);
        username.setValue(params.get(SignUpView.FIELDS.userName.name()));

        CLabel password = new CLabel(SignUpView.PASSWORD_TITLE);
        password.setDebugId(SignUpView.FIELDS.password);
        password.setValue(params.get(SignUpView.FIELDS.password.name()).replaceAll(".", "*"));

        CLabel accountType = new CLabel(SignUpView.ACCOUNT_TYPE_TITLE);
        accountType.setDebugId(SignUpView.FIELDS.accountType);
        accountType.setValue(params.get(SignUpView.FIELDS.accountType.name()));

        CLabel internalCustomer = new CLabel(SignUpView.INTERNAL_CUSTOMER_TITLE);
        internalCustomer.setDebugId(SignUpView.FIELDS.internalCustomer);
        internalCustomer.setValue(params.get(SignUpView.FIELDS.internalCustomer.name()));

        CComponent<?>[][] components = new CComponent[][] {

        { username },

        { password },

        { accountType },

        { internalCustomer },

        };

        Widget form = CForm.createDecoratedFormWidget(LabelAlignment.TOP, components, i18n.tr("Sign-up result"),
                new StringDebugId(GWTJava5Helper.getSimpleName(SignUpResultView.class)));
        add(form);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

}
