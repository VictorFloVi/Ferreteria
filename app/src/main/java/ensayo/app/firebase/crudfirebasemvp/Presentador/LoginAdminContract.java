package ensayo.app.firebase.crudfirebasemvp.Presentador;

public interface LoginAdminContract {
    interface View {
        void showErrorMessage(String message);

    }

    interface Presenter {

        void accederAdmin(String email, String password);
    }
}
