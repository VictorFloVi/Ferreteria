package ensayo.app.firebase.crudfirebasemvp.Presentador;

public interface ProductoFragmentContract {
    interface View {
        void showErrorMessage(String message);
    }

    interface Presenter {
        void listarProductos();
    }
}
