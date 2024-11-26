package ensayo.app.firebase.crudfirebasemvp.Presentador;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import ensayo.app.firebase.crudfirebasemvp.Modelo.Cliente;
import ensayo.app.firebase.crudfirebasemvp.Vista.ClientesAdmin;

public class ClienteAdminPresenter implements ClienteAdminContract.Presenter{
    private static final String CLIENTES = "clientes";
    private static final String NOMBRE = "nombre";
    private ClientesAdmin view;
    private DatabaseReference mDatabase;

    public ClienteAdminPresenter(ClientesAdmin view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void listarClientes() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child(CLIENTES);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Cliente> clientes = new ArrayList<>();

                for (DataSnapshot clienteSnapshot : dataSnapshot.getChildren()) {
                    String nombre = clienteSnapshot.child(NOMBRE).getValue(String.class);

                    Cliente cliente = new Cliente(nombre);
                    clientes.add(cliente);
                }
                // Llama al método de la Vista para mostrar los proveedores
                view.showClientes(clientes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Llama al método de la Vista para mostrar mensajes de error
                view.showErrorMessage("Error al cargar los clientes: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void clicItemListaCliente(String nombreCliente) {
        // Obtener la descripción desde la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference().child(CLIENTES);
        Query query = mDatabase.orderByChild(NOMBRE).equalTo(nombreCliente);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String apellidoCliente = snapshot.child("apellido").getValue(String.class);
                    String dniCliente = snapshot.child("dni").getValue(String.class);
                    String telefonoCliente = snapshot.child("telefono").getValue(String.class);
                    String emailCliente = snapshot.child("email").getValue(String.class);

                    // Notificar a la vista con los detalles
                    view.showDetallesClienteSeleccionado(nombreCliente, apellidoCliente, dniCliente, telefonoCliente, emailCliente);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error de Firebase aquí
            }
        });
    }

    public void autocompletarCliente(String textoBusqueda) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child(CLIENTES);

        Query query = mDatabase.orderByChild(NOMBRE).startAt(textoBusqueda).endAt(textoBusqueda + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> clientesEncontrados = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nombre = snapshot.child(NOMBRE).getValue(String.class);
                    clientesEncontrados.add(nombre);
                }

                // Notifica a la vista con las categorías encontradas
                view.showClientesEncontradosAutocompletado(clientesEncontrados);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error de Firebase aquí
            }
        });
    }

    @Override
    public void agregarCliente(String nombre, String apellido, String dni, String telefono, String email) {
        // Validar los datos (puedes agregar más validaciones según tus necesidades)
        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
            view.showErrorMessage("Todos los campos son obligatorios");
        } else {
            mDatabase = FirebaseDatabase.getInstance().getReference().child(CLIENTES);

            // Realizar una consulta para verificar si ya existe un cliente con el mismo nombre
            Query query = mDatabase.orderByChild(NOMBRE).equalTo(nombre);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Ya existe un cliente con el mismo nombre, muestra un mensaje de error
                        view.showErrorMessage("Ya existe un proveedor con ese nombre");
                    } else {
                        // No existe un cliente con el mismo nombre, procede a agregarla

                        // Crear un objeto para el producto
                        Cliente cliente = new Cliente(nombre,apellido, dni, telefono, email);

                        // Agrega la categoría con la URL de la imagen a la base de datos
                        mDatabase.push().setValue(cliente);

                        // Notifica a la vista de éxito
                        view.showSuccessMessage("Cliente agregado con éxito.");

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Manejar el error de Firebase aquí
                }
            });
        }
    }

    public void consultarCliente(String nombreCliente) {
        // Validar que el nombre de la categoría no sea nulo o esté vacío
        if (nombreCliente == null || nombreCliente.trim().isEmpty()) {
            view.showErrorMessage("Ingrese un nombre de cliente");
            return;
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child(CLIENTES);
        Query query = mDatabase.orderByChild(NOMBRE).equalTo(nombreCliente);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nombre = snapshot.child(NOMBRE).getValue(String.class);
                    String apellido = snapshot.child("apellido").getValue(String.class);
                    String dni = snapshot.child("dni").getValue(String.class);
                    String telefono = snapshot.child("telefono").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);

                    // Crear un objeto Producto con la información obtenida
                    Cliente cliente = new Cliente(nombre, apellido, dni, telefono, email);

                    // Notificar a la vista con la categoría obtenida
                    view.showConsultarCliente(cliente);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error de Firebase aquí
            }
        });
    }

    @Override
    public void editarCliente(String nombre, String apellido, String dni, String telefono, String email) {
        // Validar los datos (puedes agregar más validaciones según tus necesidades)
        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
            view.showErrorMessage("Todos los campos son obligatorios");
            return;
        }

        // Obtener la referencia al cliente en la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference().child(CLIENTES);
        Query query = mDatabase.orderByChild(NOMBRE).equalTo(nombre);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // Actualiza el ciente con la nueva URL de la imagen
                    Cliente clienteActualizado = new Cliente(nombre, apellido, dni, telefono, email);
                    snapshot.getRef().setValue(clienteActualizado);

                    // Notificar a la vista de éxito
                    view.showSuccessMessage("Cliente actualizado con éxito.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error de Firebase aquí
                view.showErrorMessage("Error al editar el cliente: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void borrarCliente(String nombre) {
        // Validar el nombre del cliente

        if (nombre.isEmpty()) {
            view.showErrorMessage("Nombre de cliente inválido");
            return;
        }

        // Obtener la referencia al producto en la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference().child(CLIENTES);
        Query query = mDatabase.orderByChild(NOMBRE).equalTo(nombre);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String clienteId = snapshot.getKey();

                    // Borrar el producto de la base de datos
                    mDatabase.child(clienteId).removeValue();

                    // Notificar a la vista de éxito
                    view.showSuccessMessage("Cliente eliminado con éxito.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error de Firebase aquí
                view.showErrorMessage("Error al borrar el cliente: " + databaseError.getMessage());
            }
        });
    }

}
