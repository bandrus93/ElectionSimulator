package com.innotech.electionsim.view;

public class MainView {
    private static final String MAIN_MENU = """

                1 - Start New Election
                2 - Review Past Elections
                3 - Settings""";
    private static final StringBuilder viewBuilder = new StringBuilder();
    private final String view;

    private MainView(String toDisplay) {
        view = toDisplay;
    }

    public static MainView getInstance(String locale) {
        if (!viewBuilder.isEmpty()) {
            viewBuilder.delete(0, viewBuilder.length());
        }
        return new MainView(viewBuilder
                .append("Welcome to ")
                .append(locale)
                .append("! What would you like to do?\n(Type a number and press 'Enter' to make a selection, or type 'q' to quit):\n")
                .append(MAIN_MENU)
                .toString());
    }

    public String getView() {
        return view;
    }
}
