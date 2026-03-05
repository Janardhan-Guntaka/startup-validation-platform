package com.validation.review.entity;

public enum ReactionType {
    WOULD_PAY("I would pay for this", "\uD83D\uDCB0", 10),
    WOULD_USE("I would use this", "\uD83D\uDC4D", 5),
    LOVE_IT("Love this idea", "\u2764\uFE0F", 3),
    INTERESTING("Interesting concept", "\uD83E\uDD14", 2),
    NEED_MORE_INFO("Need more details", "\u2753", 1),
    ALREADY_EXISTS("Similar products exist", "\uD83D\uDD04", -2),
    WONT_USE("Wouldn't use this", "\uD83D\uDC4E", -5),
    NO_PROBLEM("Doesn't solve a real problem", "\u274C", -8);

    private final String label;
    private final String icon;
    private final int weight;

    ReactionType(String label, String icon, int weight) {
        this.label = label;
        this.icon = icon;
        this.weight = weight;
    }

    public String getLabel() {
        return label;
    }

    public String getIcon() {
        return icon;
    }

    public int getWeight() {
        return weight;
    }
}
