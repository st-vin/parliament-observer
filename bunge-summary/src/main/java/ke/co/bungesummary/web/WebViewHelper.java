package ke.co.bungesummary.web;

public final class WebViewHelper {

    private WebViewHelper() {}

    public static String outcomeBadgeClass(String outcome) {
        if (outcome == null || outcome.isBlank()) {
            return "badge-default";
        }
        return switch (outcome.toUpperCase()) {
            case "PASSED", "ADOPTED", "APPROVED" -> "badge-passed";
            case "REJECTED", "DEFEATED" -> "badge-rejected";
            case "DEFERRED", "REFERRED", "POSTPONED" -> "badge-deferred";
            case "DEBATED", "DISCUSSED" -> "badge-debated";
            default -> "badge-default";
        };
    }

    public static String outcomeLabel(String outcome) {
        if (outcome == null || outcome.isBlank()) {
            return "DEBATED";
        }
        return outcome.replace('_', ' ');
    }

    public static String stanceBadgeClass(String stance) {
        if (stance == null) {
            return "stance-uncertain";
        }
        return switch (stance.toUpperCase()) {
            case "FOR" -> "stance-for";
            case "AGAINST" -> "stance-against";
            default -> "stance-uncertain";
        };
    }

    public static String summaryExcerpt(String summary, int maxLen) {
        if (summary == null || summary.isBlank()) {
            return "";
        }
        String trimmed = summary.strip();
        if (trimmed.length() <= maxLen) {
            return trimmed;
        }
        return trimmed.substring(0, maxLen).stripTrailing() + "…";
    }
}
