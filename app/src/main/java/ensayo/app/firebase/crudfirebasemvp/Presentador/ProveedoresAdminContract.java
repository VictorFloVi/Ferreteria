package ensayo.app.firebase.crudfirebasemvp.Presentador;

import java.util.List;

public interface ProveedoresAdminContract {
    interface View{

        void showProveedoresEncontradosAutocompletado(List<String> proveedores);
    }
    interface Presenter{

        void autocompletarProveedor(String textoBusqueda);

        void listarProveedores();

        void agregarProveedor(String nombre, String ruc, String telefono, String email, String direccion);

        void clicItemListaProveedor(String nombreProveedor);

        void consultarProveedor(String nombreProveedor);

        void editarProveedor(String nombre, String ruc, String telefono, String email, String direccion);

        void borrarProveedor(String nombre);
    }
}
