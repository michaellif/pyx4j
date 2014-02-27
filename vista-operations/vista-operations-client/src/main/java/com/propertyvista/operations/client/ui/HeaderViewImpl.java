package com.propertyvista.operations.client.ui;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Toolbar;

import com.propertyvista.common.client.theme.SiteViewTheme;

public class HeaderViewImpl extends HorizontalPanel implements HeaderView {

    public enum Theme {
        Gainsboro, VillageGreen, BlueCold, BrownWarm
    }

    private static final I18n i18n = I18n.get(HeaderViewImpl.class);

    private Presenter presenter;

    private HTML greetings;

    private Anchor logout;

    private Anchor login;

    private Anchor account;

    public HeaderViewImpl() {
        setStyleName(SiteViewTheme.StyleName.SiteViewHeader.name());

        Widget w;
        add(w = createLogoContainer());
        setCellHorizontalAlignment(w, HasHorizontalAlignment.ALIGN_LEFT);

        add(w = createActionsContainer());
        setCellHorizontalAlignment(w, HasHorizontalAlignment.ALIGN_RIGHT);
    }

    private Widget createLogoContainer() {
        SimplePanel logoContainer = new SimplePanel();
        logoContainer.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        logoContainer.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        logoContainer.getElement().getStyle().setMargin(5, Unit.PX);

        HTML logo = new HTML("Property Vista Software<br/>Operations");
        logo.getElement().getStyle().setCursor(Cursor.POINTER);

        logo.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.navigToLanding();
            }
        });
        logoContainer.setWidget(logo);
        return logoContainer;
    }

    private Widget createActionsContainer() {
        Toolbar toolbar = new Toolbar();
        toolbar.addStyleName(SiteViewTheme.StyleName.SiteViewAction.name());

        greetings = new HTML("");
        greetings.getElement().getStyle().setDisplay(Display.INLINE);
        greetings.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        greetings.getElement().getStyle().setMarginRight(1, Unit.EM);

        logout = new Anchor(null);
        logout.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.logout();
            }
        });
        logout.ensureDebugId("logout");
        logout.setHTML(i18n.tr("LogOut"));
        logout.setVisible(false);
        logout.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        login = new Anchor(null);
        login.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.login();
            }
        });

        login.ensureDebugId("login");
        login.setHTML(i18n.tr("Log In"));
        login.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        account = new Anchor(null);
        account.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showAccount();
            }
        });

        account.ensureDebugId("account");
        account.setHTML(i18n.tr("Account"));
        account.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        toolbar.addItem(greetings);
        toolbar.addItem(account);
        toolbar.addItem(login);
        toolbar.addItem(logout);

        return toolbar.asWidget();
    }

    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onLogedOut() {
        logout.setVisible(false);
        account.setVisible(false);
        login.setVisible(true);
        greetings.setHTML("");
    }

    @Override
    public void onLogedIn(String userName) {
        logout.setVisible(true);
        login.setVisible(false);
        account.setVisible(true);
        greetings.setHTML(i18n.tr("Welcome &nbsp;{0}", userName));
    }
}
