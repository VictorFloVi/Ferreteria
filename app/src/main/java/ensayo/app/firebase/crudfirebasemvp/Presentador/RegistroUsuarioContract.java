package ensayo.app.firebase.crudfirebasemvp.Presentador;

public interface RegistroUsuarioContract {
    interface View {
        void showErrorMessage(String message);
    }

    interface Presenter {
        void registrarUsuario(String nombreCompleto, String nombreUsuario, String email, String password);
    }
}
