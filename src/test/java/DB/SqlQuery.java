package DB;

public class SqlQuery {

    public static String getLoginCreditsFromDB(Integer recordId) {
        return "SELECT login, password FROM public.login_data WHERE id=" + recordId;
    }
}
