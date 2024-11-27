package ensayo.app.firebase.crudfirebasemvp.Presentador;

import android.net.Uri;
import android.os.Environment;


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
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ensayo.app.firebase.crudfirebasemvp.Modelo.Producto;
import ensayo.app.firebase.crudfirebasemvp.Vista.ProductosAdmin;

public class ProductoAdminPresenter implements ProductosAdminContract.Presenter {
    private static final String PRODUCTOS = "productos";
    private static final String NOMBRE = "nombre";
    private static final String IMAGENURL = "imagenUrl";
    private ProductosAdmin view;
    private DatabaseReference mDatabase;

    public ProductoAdminPresenter(ProductosAdmin view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void listarProductos() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child(PRODUCTOS);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Producto> productos = new ArrayList<>();

                for (DataSnapshot productoSnapshot : dataSnapshot.getChildren()) {
                    String nombre = productoSnapshot.child(NOMBRE).getValue(String.class);
                    String imageUrl = productoSnapshot.child(IMAGENURL).getValue(String.class);

                    Producto producto = new Producto(nombre, imageUrl);
                    productos.add(producto);
                }
                // Llama al método de la Vista para mostrar las categorías
                view.showProductos(productos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Llama al método de la Vista para mostrar mensajes de error
                view.showErrorMessage("Error al cargar las categorías: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void clicItemListaProducto(String nombreProducto) {
        // Obtener la descripción desde la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference().child(PRODUCTOS);
        Query query = mDatabase.orderByChild(NOMBRE).equalTo(nombreProducto);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String categoriaProducto = snapshot.child("categoria").getValue(String.class);
                    Double costoProducto = snapshot.child("costo").getValue(Double.class);
                    Double precVenta = snapshot.child("precioVenta").getValue(Double.class);
                    String imageUrl = snapshot.child(IMAGENURL).getValue(String.class);

                    // Notificar a la vista con los detalles
                    view.showDetallesProductoSeleccionado(nombreProducto, categoriaProducto, String.valueOf(costoProducto), String.valueOf(precVenta), imageUrl);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error de Firebase aquí
            }
        });
    }

    @Override
    public void consultarProducto(String nombreProducto) {
        // Validar que el nombre de la categoría no sea nulo o esté vacío
        if (nombreProducto == null || nombreProducto.trim().isEmpty()) {
            view.showErrorMessage("Ingrese un nombre de producto");
            return;
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child(PRODUCTOS);
        Query query = mDatabase.orderByChild(NOMBRE).equalTo(nombreProducto);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nombre = snapshot.child(NOMBRE).getValue(String.class);
                    String categoria = snapshot.child("categoria").getValue(String.class);
                    Double costo = snapshot.child("costo").getValue(Double.class);
                    Double precioventa = snapshot.child("precioVenta").getValue(Double.class);
                    String imageUrl = snapshot.child(IMAGENURL).getValue(String.class);

                    // Crear un objeto Producto con la información obtenida
                    Producto producto = new Producto(nombre, categoria, costo, precioventa, imageUrl);

                    // Notificar a la vista con la categoría obtenida
                    view.showConsultarProducto(producto);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error de Firebase aquí
            }
        });
    }

    public void autocompletarProducto(String textoBusqueda) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child(PRODUCTOS);

        Query query = mDatabase.orderByChild(NOMBRE).startAt(textoBusqueda).endAt(textoBusqueda + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> productosEncontrados = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nombre = snapshot.child(NOMBRE).getValue(String.class);
                    productosEncontrados.add(nombre);
                }

                // Notifica a la vista con las categorías encontradas
                view.showProductosEncontradosAutocompletado(productosEncontrados);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error de Firebase aquí
            }
        });
    }

    @Override
    public void agregarProducto(String nombre, String categoria, String costo, String precioVenta, String imagenUrl) {
        // Validar los datos (puedes agregar más validaciones según tus necesidades)
        if (nombre.isEmpty() || categoria.isEmpty() || costo.isEmpty() || precioVenta.isEmpty() || imagenUrl.isEmpty()) {
            view.showErrorMessage("Todos los campos son obligatorios");
        } else {
            mDatabase = FirebaseDatabase.getInstance().getReference().child(PRODUCTOS);

            // Realizar una consulta para verificar si ya existe una categoría con el mismo nombre
            Query query = mDatabase.orderByChild(NOMBRE).equalTo(nombre);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Ya existe una categoría con el mismo nombre, muestra un mensaje de error
                        view.showErrorMessage("Ya existe un producto con ese nombre");
                    } else {
                        // No existe un producto con el mismo nombre, procede a agregarla

                        // Crear un objeto para el producto
                        Producto producto = new Producto(nombre, categoria, Double.parseDouble(costo), Double.parseDouble(precioVenta), imagenUrl);

                        // Si hay una URL de imagen, realiza la carga de la imagen a Firebase Storage
                        if (!imagenUrl.isEmpty()) {
                            // Obtén una referencia al almacenamiento de Firebase
                            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(PRODUCTOS);

                            // Genera un nombre único para la imagen (puedes personalizar esto)
                            String nombreImagen = nombre + "_" + System.currentTimeMillis() + ".jpg";

                            // Sube la imagen a Firebase Storage
                            StorageReference imagenRef = storageRef.child(nombreImagen);

                            UploadTask uploadTask = imagenRef.putFile(Uri.parse(imagenUrl));
                            uploadTask.addOnSuccessListener(taskSnapshot -> {
                                // La imagen se ha subido exitosamente, ahora obtén la URL de la imagen
                                Task<Uri> downloadUrl = imagenRef.getDownloadUrl();

                                downloadUrl.addOnSuccessListener(uri -> {
                                    // Guarda la URL de la imagen en Firebase Realtime Database
                                    producto.setImagenUrl(uri.toString());

                                    // Agrega la categoría con la URL de la imagen a la base de datos
                                    mDatabase.push().setValue(producto);

                                    // Notifica a la vista de éxito
                                    view.showSuccessMessage("Producto agregado con éxito.");
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
    public void obtenerCategorias() {
        List<String> nombresCategorias = new ArrayList<>();
        nombresCategorias.add("Seleccionar");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("categorias");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nombresCategorias.clear(); // Limpiar la lista antes de agregar las nuevas categorías
                nombresCategorias.add("Seleccionar"); // Agregar la opción "Seleccionar" nuevamente
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nombreCategoria = snapshot.child(NOMBRE).getValue(String.class);
                    if (nombreCategoria != null) {
                        nombresCategorias.add(nombreCategoria);
                    }
                }
                view.mostrarCategorias(nombresCategorias);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error si es necesario
            }
        });
    }

    @Override
    public void editarProducto(String nombre, String categoria, String costo, String precioVenta, String nuevaImageUrl) {
        // Validar los datos (puedes agregar más validaciones según tus necesidades)
        if (nombre.isEmpty() || categoria.isEmpty() || costo.isEmpty() || precioVenta.isEmpty()) {
            view.showErrorMessage("Todos los campos son obligatorios");
            return;
        }

        // Obtener la referencia a la categoría en la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference().child(PRODUCTOS);
        Query query = mDatabase.orderByChild(NOMBRE).equalTo(nombre);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // Si se proporciona una nueva URL de imagen, cargar la nueva imagen
                    if (!nuevaImageUrl.isEmpty()) {
                        // Obtén una referencia al almacenamiento de Firebase
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(PRODUCTOS);

                        // Genera un nombre único para la nueva imagen (puedes personalizar esto)
                        String nombreImagen = nombre + "_" + System.currentTimeMillis() + ".jpg";

                        // Sube la nueva imagen a Firebase Storage
                        StorageReference imagenRef = storageRef.child(nombreImagen);

                        UploadTask uploadTask = imagenRef.putFile(Uri.parse(nuevaImageUrl));
                        uploadTask.addOnSuccessListener(taskSnapshot -> {
                            // La nueva imagen se ha subido exitosamente, ahora obtén la URL de la nueva imagen
                            imagenRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                // Actualiza la categoría con la nueva URL de la imagen
                                Producto productoActualizado = new Producto(nombre, categoria, Double.parseDouble(costo), Double.parseDouble(precioVenta), uri.toString());
                                snapshot.getRef().setValue(productoActualizado);

                                // Notificar a la vista de éxito
                                view.showSuccessMessage("Producto actualizado con éxito.");
                            });
                        });
                    } else {
                        // Si no se proporciona una nueva URL de imagen, solo actualiza otros detalles
                        String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                        // Verifica si la URL de imagen actual no es nula antes de actualizar
                        if (imageUrl != null) {
                            Producto productoActualizado = new Producto(nombre, categoria, Double.parseDouble(costo), Double.parseDouble(precioVenta), imageUrl);
                            snapshot.getRef().setValue(productoActualizado);
                            view.showSuccessMessage("Producto actualizado con éxito.");
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
    public void borrarProducto(String nombre) {
        // Validar el nombre del producto

        if (nombre.isEmpty()) {
            view.showErrorMessage("Nombre de producto inválido");
            return;
        }

        // Obtener la referencia al producto en la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference().child(PRODUCTOS);
        Query query = mDatabase.orderByChild(NOMBRE).equalTo(nombre);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String productoId = snapshot.getKey();

                    // Borrar el producto de la base de datos
                    mDatabase.child(productoId).removeValue();

                    // Notificar a la vista de éxito
                    view.showSuccessMessage("Producto eliminado con éxito.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error de Firebase aquí
                view.showErrorMessage("Error al borrar el producto: " + databaseError.getMessage());
            }
        });
    }



    public void obtenerProductosYGenerarPDF() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Map<String, Object>> productos = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nombre = snapshot.child(NOMBRE).getValue(String.class);
                    String categoria = snapshot.child("categoria").getValue(String.class);
                    Double costo = snapshot.child("costo").getValue(Double.class);
                    Double precioVenta = snapshot.child("precioVenta").getValue(Double.class);

                    Map<String, Object> producto = new HashMap<>();
                    producto.put(NOMBRE, nombre);
                    producto.put("categoria", categoria);
                    producto.put("costo", costo);
                    producto.put("precioVenta", precioVenta);
                    productos.add(producto);
                }

                generarPDFYSubirFirebase(productos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                view.showErrorMessage("Error al cargar productos: " + error.getMessage());
            }
        });
    }

    private void generarPDFYSubirFirebase(List<Map<String, Object>> productos) {
        try {
            // Crear un archivo temporal para el PDF
            String pdfFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/productos.pdf";

            // Generar el PDF
            PdfDocument pdfDocument = new PdfDocument(new PdfWriter(pdfFilePath));
            Document document = new Document(pdfDocument);

            document.add(new Paragraph("Lista de Productos"));
            for (Map<String, Object> producto : productos) {
                String nombre = (String) producto.get(NOMBRE);
                String categoria = (String) producto.get("categoria");
                Double costo = (Double) producto.get("costo");
                Double precioVenta = (Double) producto.get("precioVenta");

                document.add(new Paragraph("Nombre: " + nombre));
                document.add(new Paragraph("Categoría: " + categoria));
                document.add(new Paragraph("Costo: " + costo));
                document.add(new Paragraph("Precio de Venta: " + precioVenta));
                document.add(new Paragraph(""));
            }

            document.close();

            // Subir el PDF a Firebase Storage
            subirPDFaFirebase(new File(pdfFilePath));
            abrirPDF(pdfFilePath);



        } catch (IOException e) {
            view.showErrorMessage("Error al generar el PDF: " + e.getMessage());
        }
    }

    private void subirPDFaFirebase(File pdfFile) {
        String pdfFileName = "productos_" + System.currentTimeMillis() + ".pdf";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("pdfs/" + pdfFileName);


        //StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("pdfs/productos.pdf");
        storageRef.putFile(Uri.fromFile(pdfFile))
                .addOnSuccessListener(taskSnapshot -> view.showSuccessMessage("PDF subido correctamente a Firebase"))
                .addOnFailureListener(e -> view.showErrorMessage("Error al subir el PDF: " + e.getMessage()));
    }

    public void abrirPDF(String rutaPDF) {
        File pdfFile = new File(rutaPDF);

        if (pdfFile.exists()) {
            view.mostrarPDF(rutaPDF); // Llama a la vista para abrir el PDF
        } else {
            view.showErrorMessage("El archivo PDF no existe en la ruta especificada.");
        }
    }



}
