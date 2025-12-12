public class DashboardContext implements mainOrganizerViewContext {

    @Override
    public boolean shouldShowButton(String buttonName) {
        return switch (buttonName) {
            case "bookPlaces" -> false;
            default -> true;
        };
    }

    @Override
    public String getContextName() {
        return "Dashboard";
    }    
}
