
public class Session {
    private static int organizerId;
    private static String username;

    public static void setSession(int id, String user) {
        organizerId = id;
        username = user;
    }

    public static int getOrganizerId() {
        return organizerId;
    }

    public static String getUsername() {
        return username;
    }

    public static void clear() {
        organizerId = 0;
        username = null;
    }
}

