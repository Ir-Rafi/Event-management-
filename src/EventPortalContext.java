public class EventPortalContext implements mainOrganizerViewContext {
    @Override
    public boolean shouldShowButton(String buttonName) {
        // From event portal - show all buttons
        return switch (buttonName) {
            case "chat" -> false;
            default -> true;
        };
    }
    
    @Override
    public String getContextName() {
        return "Event Portal";
    }
}