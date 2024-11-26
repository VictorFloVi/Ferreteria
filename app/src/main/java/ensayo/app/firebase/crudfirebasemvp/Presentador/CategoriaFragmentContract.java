package ensayo.app.firebase.crudfirebasemvp.Presentador;

public interface CategoriaFragmentContract {
    interface View {
        void showErrorMessage(String message);
    }

    interface Presenter {
        void listarCategorias();
    }
}
