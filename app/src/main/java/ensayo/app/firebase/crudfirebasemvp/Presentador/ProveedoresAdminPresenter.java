package ensayo.app.firebase.crudfirebasemvp.Presentador;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import ensayo.app.firebase.crudfirebasemvp.Modelo.Producto;
import ensayo.app.firebase.crudfirebasemvp.Modelo.Proveedor;
import ensayo.app.firebase.crudfirebasemvp.Vista.ProveedoresAdmin;

public class ProveedoresAdminPresenter implements ProveedoresAdminContract.Presenter{
    private static final String PROVEEDORES = "proveedores";
    private static final String NOMBRE = "nombre";
    private ProveedoresAdmin view;
    private DatabaseReference mDatabase;
    public ProveedoresAdminPresenter(ProveedoresAdmin view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void listarProveedores() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child(PROVEEDORES);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Proveedor> proveedores = new ArrayList<>();

                for (DataSnapshot proveedorSnapshot : dataSnapshot.getChildren()) {
                    String nombre = proveedorSnapshot.child(NOMBRE).getValue(String.class);

                    Proveedor proveedor = new Proveedor(nombre);
                    proveedores.add(proveedor);
                }
                // Llama al método de la Vista para mostrar los proveedores
                view.showProveedores(proveedores);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Llama al método de la Vista para mostrar mensajes de error
                view.showErrorMessage("Error al cargar los proveedores: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void clicItemListaProveedor(String nombreProveedor) {
        // Obtener la descripción desde la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference().child(PROVEEDORES);
        Query query = mDatabase.orderByChild(NOMBRE).equalTo(nombreProveedor);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String rucProveedor = snapshot.child("ruc").getValue(String.class);
                    String telefonoProveedor = snapshot.child("telefono").getValue(String.class);
                    String emailProveedor = snapshot.child("email").getValue(String.class);
                    String direccionProveedor = snapshot.child("email").getValue(String.class);

                    // Notificar a la vista con los detalles
                    view.showDetallesProveedorSeleccionado(nombreProveedor, rucProveedor, telefonoProveedor, emailProveedor, direccionProveedor);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error de Firebase aquí
            }
        });
    }

    @Override
    public void consultarProveedor(String nombreProveedor) {
        // Validar que el nombre de la categoría no sea nulo o esté vacío
        if (nombreProveedor == null || nombreProveedor.trim().isEmpty()) {
            view.showErrorMessage("Ingrese un nombre de producto");
            return;
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child(PROVEEDORES);
        Query query = mDatabase.orderByChild(NOMBRE).equalTo(nombreProveedor);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nombre = snapshot.child(NOMBRE).getValue(String.class);
                    String ruc = snapshot.child("ruc").getValue(String.class);
                    String telefono = snapshot.child("telefono").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String direccion = snapshot.child("direccion").getValue(String.class);

                    // Crear un objeto Producto con la información obtenida
                    Proveedor proveedor = new Proveedor(nombre, ruc, telefono, email, direccion);

                    // Notificar a la vista con la categoría obtenida
                    view.showConsultarProveedor(proveedor);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error de Firebase aquí
            }
        });
    }

    public void autocompletarProveedor(String textoBusqueda) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child(PROVEEDORES);

        Query query = mDatabase.orderByChild(NOMBRE).startAt(textoBusqueda).endAt(textoBusqueda + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> proveedoresEncontrados = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nombre = snapshot.child(NOMBRE).getValue(String.class);
                    proveedoresEncontrados.add(nombre);
                }

                // Notifica a la vista con las categorías encontradas
                view.showProveedoresEncontradosAutocompletado(proveedoresEncontrados);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error de Firebase aquí
            }
        });
    }

    @Override
    public void agregarProveedor(String nombre, String ruc, String telefono, String email, String direccion) {
        // Validar los datos (puedes agregar más validaciones según tus necesidades)
        if (nombre.isEmpty() || ruc.isEmpty() || telefono.isEmpty() || email.isEmpty() || direccion.isEmpty()) {
            view.showErrorMessage("Todos los campos son obligatorios");
        } else {
            mDatabase = FirebaseDatabase.getInstance().getReference().child(PROVEEDORES);

            // Realizar una consulta para verificar si ya existe una categoría con el mismo nombre
            Query query = mDatabase.orderByChild(NOMBRE).equalTo(nombre);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Ya existe un proveedor con el mismo nombre, muestra un mensaje de error
                        view.showErrorMessage("Ya existe un proveedor con ese nombre");
                    } else {
                        // No existe un producto con el mismo nombre, procede a agregarla

                        // Crear un objeto para el producto
                        Proveedor proveedor = new Proveedor(nombre, ruc, telefono, email, direccion);

                        // Agrega la categoría con la URL de la imagen a la base de datos
                        mDatabase.push().setValue(proveedor);

                        // Notifica a la vista de éxito
                        view.showSuccessMessage("Producto agregado con éxito.");

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Manejar el error de Firebase aquí
                }
            });
        }
    }

    @Override
    public void editarProveedor(String nombre, String ruc, String telefono, String email, String direccion) {
        // Validar los datos (puedes agregar más validaciones según tus necesidades)
        if (nombre.isEmpty() || ruc.isEmpty() || telefono.isEmpty() || email.isEmpty() || direccion.isEmpty()) {
            view.showErrorMessage("Todos los campos son obligatorios");
            return;
        }

        // Obtener la referencia a la categoría en la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference().child(PROVEEDORES);
        Query query = mDatabase.orderByChild(NOMBRE).equalTo(nombre);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // Actualiza la categoría con la nueva URL de la imagen
                    Proveedor proveedorActualizado = new Proveedor(nombre, ruc, telefono, email, direccion);
                    snapshot.getRef().setValue(proveedorActualizado);

                    // Notificar a la vista de éxito
                    view.showSuccessMessage("Proveedor actualizado con éxito.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error de Firebase aquí
                view.showErrorMessage("Error al editar el proveedor: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void borrarProveedor(String nombre) {
        // Validar el nombre del proveedor

        if (nombre.isEmpty()) {
            view.showErrorMessage("Nombre de proveedor inválido");
            return;
        }

        // Obtener la referencia al producto en la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference().child(PROVEEDORES);
        Query query = mDatabase.orderByChild(NOMBRE).equalTo(nombre);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String proveedorId = snapshot.getKey();

                    // Borrar el producto de la base de datos
                    mDatabase.child(proveedorId).removeValue();

                    // Notificar a la vista de éxito
                    view.showSuccessMessage("Proveedor eliminado con éxito.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error de Firebase aquí
                view.showErrorMessage("Error al borrar el proveedor: " + databaseError.getMessage());
            }
        });
    }

}
