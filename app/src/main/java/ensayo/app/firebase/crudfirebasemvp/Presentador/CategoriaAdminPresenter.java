package ensayo.app.firebase.crudfirebasemvp.Presentador;

import android.net.Uri;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ensayo.app.firebase.crudfirebasemvp.Modelo.Categoria;
import ensayo.app.firebase.crudfirebasemvp.Vista.CategoriaAdmin;

public class CategoriaAdminPresenter implements CategoriaAdminContract.Presenter {
    private static final String CATEGORIAS = "categorias";
    private static final String NOMBRE = "nombre";
    private CategoriaAdmin view;
    private DatabaseReference mDatabase;

    public CategoriaAdminPresenter(CategoriaAdmin view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public void agregarCategoria(String nombre, String descripcion, String imageUrl) {
        // Validar los datos (puedes agregar más validaciones según tus necesidades)
        if (nombre.isEmpty() || descripcion.isEmpty() || imageUrl.isEmpty()) {
            view.showErrorMessage("Todos los campos son obligatorios");
        } else {
            mDatabase = FirebaseDatabase.getInstance().getReference().child(CATEGORIAS);

            // Realizar una consulta para verificar si ya existe una categoría con el mismo nombre
            Query query = mDatabase.orderByChild("nombre").equalTo(nombre);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Ya existe una categoría con el mismo nombre, muestra un mensaje de error
                        view.showErrorMessage("Ya existe una categoría con ese nombre");
                    } else {
                        // No existe una categoría con el mismo nombre, procede a agregarla

                        // Crear un objeto para la categoría
                        Categoria categoria = new Categoria(nombre, descripcion, imageUrl);

                        // Si hay una URL de imagen, realiza la carga de la imagen a Firebase Storage
                        if (!imageUrl.isEmpty()) {
                            // Obtén una referencia al almacenamiento de Firebase
                            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(CATEGORIAS);

                            // Genera un nombre único para la imagen (puedes personalizar esto)
                            String nombreImagen = nombre + "_" + System.currentTimeMillis() + ".jpg";

                            // Sube la imagen a Firebase Storage
                            StorageReference imagenRef = storageRef.child(nombreImagen);

                            UploadTask uploadTask = imagenRef.putFile(Uri.parse(imageUrl));
                            uploadTask.addOnSuccessListener(taskSnapshot -> {
                                // La imagen se ha subido exitosamente, ahora obtén la URL de la imagen
                                Task<Uri> downloadUrl = imagenRef.getDownloadUrl();

                                downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Guarda la URL de la imagen en Firebase Realtime Database
                                        categoria.setImagenUrl(uri.toString());

                                        // Agrega la categoría con la URL de la imagen a la base de datos
                                        mDatabase.push().setValue(categoria);

                                        // Notifica a la vista de éxito
                                        view.showSuccessMessage("Categoría agregada con éxito.");
                                    }
                                });
                            });
                        }
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
    public void listarCategorias() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child(CATEGORIAS);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Categoria> categorias = new ArrayList<>();

                for (DataSnapshot categoriaSnapshot : dataSnapshot.getChildren()) {
                    String nombre = categoriaSnapshot.child("nombre").getValue(String.class);
                    String imageUrl = categoriaSnapshot.child("imagenUrl").getValue(String.class);

                    Categoria categoria = new Categoria(nombre, imageUrl);
                    categorias.add(categoria);
                }
                // Llama al método de la Vista para mostrar las categorías
                view.showCategorias(categorias);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Llama al método de la Vista para mostrar mensajes de error
                view.showErrorMessage("Error al cargar las categorías: " + databaseError.getMessage());
            }
        });
    }


    @Override
    public void clicItemListaCategoria(String nombreCategoria) {
        // Obtener la descripción desde la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference().child(CATEGORIAS);
        Query query = mDatabase.orderByChild("nombre").equalTo(nombreCategoria);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String descripcionCategoria = snapshot.child("descripcion").getValue(String.class);
                    String imageUrl = snapshot.child("imagenUrl").getValue(String.class);

                    // Notificar a la vista con los detalles
                    view.showDetallesCategoriaSeleccionada(nombreCategoria, descripcionCategoria, imageUrl);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error de Firebase aquí
            }
        });
    }


    @Override
    public void consultarCategoria(String nombreCategoria) {
        // Validar que el nombre de la categoría no sea nulo o esté vacío
        if (nombreCategoria == null || nombreCategoria.trim().isEmpty()) {
            view.showErrorMessage("Ingrese un nombre de categoría");
            return;
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child(CATEGORIAS);
        Query query = mDatabase.orderByChild("nombre").equalTo(nombreCategoria);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    String descripcion = snapshot.child("descripcion").getValue(String.class);
                    String imageUrl = snapshot.child("imagenUrl").getValue(String.class);

                    // Crear un objeto Categoria con la información obtenida
                    Categoria categoria = new Categoria(nombre, descripcion, imageUrl);

                    // Notificar a la vista con la categoría obtenida
                    view.showConsultarCategoria(categoria);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error de Firebase aquí
            }
        });
    }

    public void autocompletarCategoria(String textoBusqueda) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child(CATEGORIAS);

        Query query = mDatabase.orderByChild("nombre").startAt(textoBusqueda).endAt(textoBusqueda + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> categoriasEncontradas = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    categoriasEncontradas.add(nombre);
                }

                // Notifica a la vista con las categorías encontradas
                view.showCategoriasEncontradasAutocompletado(categoriasEncontradas);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error de Firebase aquí
            }
        });
    }

    @Override
    public void editarCategoria(String nombre, String descripcion, String nuevaImageUrl) {
        // Validar los datos (puedes agregar más validaciones según tus necesidades)
        if (nombre.isEmpty() || descripcion.isEmpty()) {
            view.showErrorMessage("Todos los campos son obligatorios");
            return;
        }

        // Obtener la referencia a la categoría en la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference().child(CATEGORIAS);
        Query query = mDatabase.orderByChild("nombre").equalTo(nombre);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // Si se proporciona una nueva URL de imagen, cargar la nueva imagen
                    if (!nuevaImageUrl.isEmpty()) {
                        // Obtén una referencia al almacenamiento de Firebase
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(CATEGORIAS);

                        // Genera un nombre único para la nueva imagen (puedes personalizar esto)
                        String nombreImagen = nombre + "_" + System.currentTimeMillis() + ".jpg";

                        // Sube la nueva imagen a Firebase Storage
                        StorageReference imagenRef = storageRef.child(nombreImagen);

                        UploadTask uploadTask = imagenRef.putFile(Uri.parse(nuevaImageUrl));

                        uploadTask.addOnSuccessListener(taskSnapshot ->
                                // La nueva imagen se ha subido exitosamente, ahora obtén la URL de la nueva imagen
                                imagenRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    // Actualiza la categoría con la nueva URL de la imagen
                                    Categoria categoriaActualizada = new Categoria(nombre, descripcion, uri.toString());
                                    snapshot.getRef().setValue(categoriaActualizada);
                                    // Notificar a la vista de éxito
                                    view.showSuccessMessage("Categoría actualizada con éxito.");
                                })
                        );


                    } else {
                        // Si no se proporciona una nueva URL de imagen, solo actualiza otros detalles
                        String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                        // Verifica si la URL de imagen actual no es nula antes de actualizar
                        if (imageUrl != null) {
                            Categoria categoriaActualizada = new Categoria(nombre, descripcion, imageUrl);
                            snapshot.getRef().setValue(categoriaActualizada);
                            view.showSuccessMessage("Categoría actualizada con éxito.");
                        } else {
                            view.showErrorImage("Debes actualizar la imagen.");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error de Firebase aquí
                view.showErrorMessage("Error al editar la categoría: " + databaseError.getMessage());
            }
        });
    }


    @Override
    public void borrarCategoria(String nombre) {
        // Validar el nombre de la categoría

        if (nombre.isEmpty()) {
            view.showErrorMessage("Nombre de categoría inválido");
            return;
        }

        // Obtener la referencia a la categoría en la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference().child(CATEGORIAS);
        Query query = mDatabase.orderByChild("nombre").equalTo(nombre);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String categoriaId = snapshot.getKey();

                    // Borrar la categoría de la base de datos
                    mDatabase.child(categoriaId).removeValue();

                    // Notificar a la vista de éxito
                    view.showSuccessMessage("Categoría eliminada con éxito.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error de Firebase aquí
                view.showErrorMessage("Error al borrar la categoría: " + databaseError.getMessage());
            }
        });
    }

    public void generarPDFCategorias() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child(CATEGORIAS);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Map<String, Object>> categoriasMapList = new ArrayList<>();

                for (DataSnapshot categoriaSnapshot : dataSnapshot.getChildren()) {
                    // Obtener los datos relevantes de cada producto
                    String nombre = categoriaSnapshot.child(NOMBRE).getValue(String.class);
                    String descripcion = categoriaSnapshot.child("descripcion").getValue(String.class);

                    //String imageUrl = productoSnapshot.child(IMAGENURL).getValue(String.class);

                    // Agregar los datos a un mapa para facilitar la creación del PDF
                    Map<String, Object> categoriaMap = new HashMap<>();
                    categoriaMap.put(NOMBRE, nombre);
                    categoriaMap.put("descripcion", descripcion);

                    //productoMap.put(IMAGENURL, imageUrl);

                    categoriasMapList.add(categoriaMap);
                }

                // Llama al método de la Vista para generar el PDF
                view.generarPDFCategorias(categoriasMapList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error de Firebase aquí
                view.showErrorMessage("Error al cargar los productos: " + databaseError.getMessage());
            }
        });
    }


}
