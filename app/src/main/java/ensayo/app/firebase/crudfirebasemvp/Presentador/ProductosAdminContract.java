package ensayo.app.firebase.crudfirebasemvp.Presentador;

import android.net.Uri;
import java.util.List;
import ensayo.app.firebase.crudfirebasemvp.Modelo.Producto;

public interface ProductosAdminContract {
    interface View {
        void showSuccessMessage(String message);

        void showErrorMessage(String message);

        void mostrarCategorias(List<String> categorias);

        void showProductos(List<Producto> productos);

        void showConsultarProducto(Producto producto);

        void showErrorImage(String message);

        void showNotification(String message);

        void showSelectedImage(Uri imageUri);
        void mostrarPDF(String mensaje);

        void showProductosEncontradosAutocompletado(List<String> productos);

        void showDetallesProductoSeleccionado(String nombre, String categoriaProducto, String costoProducto, String precVenta, String imageUrl);
    }

    interface Presenter {
        void agregarProducto(String nombre, String categoria, String costo, String precioVenta, String imagenUrl);

        void listarProductos();

        void obtenerCategorias();

        void autocompletarProducto(String textoBusqueda);

        void clicItemListaProducto(String nombreProducto);

        void consultarProducto(String nombreProducto);

        void editarProducto(String nombre, String categoria, String costo, String precioVenta, String imageUrl);

        void borrarProducto(String nombre);


        //void generarPDFProductos();
        void obtenerProductosYGenerarPDF();
    }
}
