package com.propertyvista.operations.client.ui;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.layout.RiaLayoutPanelTheme;
import com.pyx4j.widgets.client.Anchor;

public class TopRightActionsViewImpl extends FlowPanel implements TopRightActionsView {

    public enum Theme {
        Gainsboro, VillageGreen, BlueCold, BrownWarm
    }

    private static final I18n i18n = I18n.get(TopRightActionsViewImpl.class);

    private Presenter presenter;

    private final HTML greetings;

    private final Anchor logout;

    private final Anchor login;

    private final Anchor account;

    private final Anchor settings;

//    private final Image message;

//    private final Image alert;

//    private final SearchBox search;

    private final Theme otherTheme = Theme.BrownWarm;

    public TopRightActionsViewImpl() {
        setStyleName(RiaLayoutPanelTheme.StyleName.SiteViewAction.name());
        setSize("100%", "100%");

        HorizontalPanel container = new HorizontalPanel();
        container.getElement().getStyle().setFloat(Style.Float.RIGHT);
        container.setSize("30%", "100%");

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

        settings = new Anchor(null);
        settings.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showSettings();
            }
        });

//        alert = new Image(AdminImages.INSTANCE.alert());
//        alert.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                presenter.showAlerts();
//            }
//        });
//        //  alert.getElement().getStyle().setMarginRight(1, Unit.EM);
//        alert.getElement().getStyle().setCursor(Cursor.POINTER);
//
//        message = new Image(AdminImages.INSTANCE.message());
//        message.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                presenter.showMessages();
//            }
//        });
//        //    message.getElement().getStyle().setMarginRight(1, Unit.EM);
//        //   message.getElement().getStyle().setMarginLeft(1, Unit.EM);
//        message.getElement().getStyle().setCursor(Cursor.POINTER);
//
//        search = new SearchBox();
//
//        /**
//         * the following set of wrappers keep login/logout group relatively steady when
//         * the elements right of it disappear
//         */
//        SimplePanel searchwr = new SimplePanel();
//        searchwr.getElement().setAttribute("style", "min-width:12em");
//        searchwr.add(search);
//
//        SimplePanel messagewr = new SimplePanel();
//        messagewr.getElement().setAttribute("style", "min-width:3em");
//        messagewr.add(message);
//
//        SimplePanel alertwr = new SimplePanel();
//        alertwr.getElement().setAttribute("style", "min-width:3em");
//        alertwr.add(alert);

        container.add(greetings);
        container.add(account);
        container.add(settings);
        container.add(login);
        container.add(logout);
//        container.add(searchwr);
//        container.add(messagewr);
//        container.add(alertwr);

        add(container);
    }

    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onLogedOut() {
        logout.setVisible(false);
//        alert.setVisible(false);
//        message.setVisible(false);
//        search.setVisible(false);
        account.setVisible(false);
        settings.setVisible(false);
        login.setVisible(true);
        greetings.setHTML("");
    }

    @Override
    public void onLogedIn(String userName) {
        logout.setVisible(true);
        login.setVisible(false);
//        alert.setVisible(true);
//        message.setVisible(true);
//        search.setVisible(true);
        account.setVisible(true);
        settings.setVisible(true);
        greetings.setHTML(i18n.tr("Welcome &nbsp;{0}", userName));
    }
}
