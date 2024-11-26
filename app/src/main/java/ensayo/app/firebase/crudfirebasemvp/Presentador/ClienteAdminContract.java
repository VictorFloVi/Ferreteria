package ensayo.app.firebase.crudfirebasemvp.Presentador;

public interface ClienteAdminContract {
    interface View{

    }
    interface Presenter{

        void listarClientes();

        void clicItemListaCliente(String nombreCliente);

        void autocompletarCliente(String textoBusqueda);

        void agregarCliente(String nombre, String apellido, String dni, String telefono, String email);

        void consultarCliente(String nombreCliente);

        void editarCliente(String nombre, String apellido, String dni, String telefono, String email);

        void borrarCliente(String nombre);


    }
}
