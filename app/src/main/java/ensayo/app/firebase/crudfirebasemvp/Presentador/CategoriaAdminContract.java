package ensayo.app.firebase.crudfirebasemvp.Presentador;

import java.util.List;

import ensayo.app.firebase.crudfirebasemvp.Modelo.Categoria;

public interface CategoriaAdminContract {
    interface View {
        void showErrorMessage(String message);

        void showSuccessMessage(String message);

        void showErrorImage(String message);

        void showConsultarCategoria(Categoria categoria);

        void showCategorias(List<Categoria> categorias);

        void showDetallesCategoriaSeleccionada(String nombre, String descripcion, String imageUrl);

        void showCategoriasEncontradasAutocompletado(List<String> categorias);


    }

    interface Presenter {
        void agregarCategoria(String nombre, String descripcion, String imageUrl);

        void listarCategorias();

        void clicItemListaCategoria(String nombreCategoria);

        void consultarCategoria(String nombreCategoria);

        void autocompletarCategoria(String textoBusqueda);

        void editarCategoria(String nombre, String descripcion, String imageUrl);

        void borrarCategoria(String nombre);

        void generarPDFCategorias();
    }
}
