package com.propertyvista.portal.tester.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.StringDebugId;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.forms.client.ui.CHyperlink;

public class TopRightActionsViewImpl extends VerticalPanel implements TopRightActionsView {

    private static I18n i18n = I18nFactory.getI18n(TopRightActionsViewImpl.class);

    private Presenter presenter;

    public TopRightActionsViewImpl() {

        CHyperlink signUp = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.goToSignUp();
            }
        });
        signUp.setDebugId(new StringDebugId("signup"));

        signUp.setValue(i18n.tr("Sign Up"));
        CHyperlink whatever = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                //empty
            }
        });
        whatever.setValue(i18n.tr("Whatever link"));

        CComponent<?>[][] components = new CComponent[][] {

        { signUp, whatever }

        };

        Widget form = CForm.createFormWidget(LabelAlignment.TOP, components, new StringDebugId(GWTJava5Helper.getSimpleName(TopRightActionsView.class)));
        add(form);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

}
