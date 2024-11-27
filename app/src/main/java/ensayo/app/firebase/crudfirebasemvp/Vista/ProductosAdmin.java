package ensayo.app.firebase.crudfirebasemvp.Vista;

import android.content.ActivityNotFoundException;
import android.content.Intent;


import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ensayo.app.firebase.crudfirebasemvp.Modelo.Producto;
import ensayo.app.firebase.crudfirebasemvp.Presentador.ProductoAdminPresenter;
import ensayo.app.firebase.crudfirebasemvp.Presentador.ProductosAdminContract;
import ensayo.app.firebase.crudfirebasemvp.R;

public class ProductosAdmin extends AppCompatActivity implements ProductosAdminContract.View {

    private static final String PDF_FILE_PATH = "/sdcard/productos_list.pdf";
    private static final String IMAGENURL = "imagenUrl";
    private static final int GALLERY_INTENT = 1;
    private static final String NOMBRE = "nombre";
    private ListView lvlistado;
    private AutoCompleteTextView etNombreProd;
    EditText etCosto;
    EditText etPrecioVenta;
    private Spinner spinnerCategorias;
    Button btnAgregarProd;
    Button btnConsultarProd;
    Button btnEditarProd;
    Button btnBorrarProd;
    Button btnGenerarPdf;


    private ImageView imaProducto;
    private Uri selectedImageUri = null;
    private ProductosAdminContract.Presenter presenter;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos_admin);

        lvlistado = findViewById(R.id.lvListadoProd);
        etNombreProd = findViewById(R.id.etNombreProd);
        etCosto = findViewById(R.id.etCosto);
        etPrecioVenta = findViewById(R.id.etPrecioVenta);
        btnAgregarProd = findViewById(R.id.btnAgregarProd);
        btnConsultarProd = findViewById(R.id.btnConsultarProd);
        btnEditarProd = findViewById(R.id.btnEditarProd);
        btnBorrarProd = findViewById(R.id.btnBorrarProd);
        btnGenerarPdf = findViewById(R.id.btnGenerarPdf);
        imaProducto = findViewById(R.id.imaProducto);
        presenter = new ProductoAdminPresenter(this);
        presenter.listarProductos();

        //Ver la lista de categorías en el Spinner
        spinnerCategorias = findViewById(R.id.spinnerCategorias);
        presenter.obtenerCategorias();


        //Detecta cuando se selecciona un elemento de la lista
        lvlistado.setOnItemClickListener((parent, view, position, id) -> {
            Map<String, Object> producto = (Map<String, Object>) parent.getItemAtPosition(position);
            String nombreProducto = (String) producto.get(NOMBRE);

            // Llama al método del presentador para obtener detalles de la categoría
            presenter.clicItemListaProducto(nombreProducto);
        });

        //Detecta los cambios en el texto del AutoCompleteTextView
        etNombreProd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String textoBusqueda = s.toString().trim();
                presenter.autocompletarProducto(textoBusqueda);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        imaProducto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_INTENT);
        });

        btnAgregarProd.setOnClickListener(v -> {

            String nombre = etNombreProd.getText().toString().trim();
            String categoria = spinnerCategorias.getSelectedItem().toString().trim();
            String costo = etCosto.getText().toString().trim();
            String precioVenta = etPrecioVenta.getText().toString().trim();
            String imagenUrl;
            if (selectedImageUri != null) {
                imagenUrl = selectedImageUri.toString();
            } else {
                imagenUrl = "";
            }
            presenter.agregarProducto(nombre, categoria, costo, precioVenta, imagenUrl);
        });


        btnConsultarProd.setOnClickListener(v -> {
            String nombreProducto = etNombreProd.getText().toString().trim();
            presenter.consultarProducto(nombreProducto);
        });

        btnEditarProd.setOnClickListener(v -> {
            String nombre = etNombreProd.getText().toString().trim();
            String categoria = spinnerCategorias.getSelectedItem().toString().trim();
            String costo = etCosto.getText().toString().trim();
            String precioventa = etPrecioVenta.getText().toString().trim();

            String imageUrl;
            if (selectedImageUri != null) {
                imageUrl = selectedImageUri.toString();
            } else {
                imageUrl = "";
            }
            presenter.editarProducto(nombre, categoria, costo, precioventa, imageUrl);

        });

        btnBorrarProd.setOnClickListener(v -> {
            String nombre = etNombreProd.getText().toString().trim();
            presenter.borrarProducto(nombre);
            clearEditTextFields();
        });


        btnGenerarPdf.setOnClickListener(v -> {
            // Llama al método para obtener la lista de productos y generar el PDF
            //presenter.generarPDFProductos();
            presenter.obtenerProductosYGenerarPDF();

        });

    }


    @Override
    public void showProductos(List<Producto> productos) {
        // Crear un adaptador personalizado para mostrar las categorías en la ListView
        List<Map<String, Object>> productosMapList = new ArrayList<>();
        for (Producto producto : productos) {
            Map<String, Object> productoMap = new HashMap<>();
            productoMap.put(NOMBRE, producto.getNombre());
            productoMap.put("imagenUrl", producto.getImagenUrl());
            productosMapList.add(productoMap);
        }

        String[] from = {NOMBRE, "imagenUrl"};
        int[] to = {R.id.tvNombrep, R.id.ivImagenp};

        adapter = new SimpleAdapter(this, productosMapList,
                R.layout.lista_producto_item, from, to);

        adapter.setViewBinder((view, data, textRepresentation) -> {
            if (view.getId() == R.id.ivImagenp) {
                ImageView imageView = (ImageView) view;
                String imageUrl = (String) data;

                Picasso.get().load(imageUrl).into(imageView);

                return true; // Indica que hemos gestionado la vista
            }
            return false;
        });
        lvlistado.setAdapter(adapter);
    }

    @Override
    public void mostrarCategorias(List<String> categorias) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategorias.setAdapter(adapter);
    }

    @Override
    public void showSuccessMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        clearEditTextFields();
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNotification(String message) {

    }

    @Override
    public void showSelectedImage(Uri imageUri) {

    }

    @Override
    public void showErrorImage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        imaProducto.setImageBitmap(null);
    }

    //Carga la imagen en el ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK && data != null) {
            // Obtiene la URI de la imagen seleccionada
            selectedImageUri = data.getData();

            // Carga la imagen en el ImageView
            Picasso.get()
                    .load(selectedImageUri)
                    .fit()
                    .centerCrop()
                    .into(imaProducto);
        }
    }

    // Método para limpiar los EditText
    public void clearEditTextFields() {
        etNombreProd.setText("");
        etCosto.setText("");
        etPrecioVenta.setText("");
        spinnerCategorias.setSelection(0);
        imaProducto.setImageBitmap(null);
    }

    @Override
    public void showProductosEncontradosAutocompletado(List<String> productos) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, productos);
        etNombreProd.setAdapter(adapter);

    }

    @Override
    public void showDetallesProductoSeleccionado(String nombre, String categoriaProducto, String costoProducto, String precVenta, String imageUrl) {

        // Mostrar la información en los campos correspondientes
        etNombreProd.setText(nombre);
        // Obtener el índice de la categoría seleccionada en el Spinner
        int index = ObtenerIndiceCategoria(categoriaProducto);
        spinnerCategorias.setSelection(index);
        etCosto.setText(costoProducto);
        etPrecioVenta.setText(precVenta);

        // Cargar la imagen utilizando Picasso desde la URL almacenada en Firebase Storage
        Picasso.get()
                .load(imageUrl)
                .fit()
                .centerCrop()
                .into(imaProducto);
    }

    // Método para obtener el índice de la categoría en el Spinner
    private int ObtenerIndiceCategoria(String categoria) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerCategorias.getAdapter();
        return adapter.getPosition(categoria);
    }

    @Override
    public void showConsultarProducto(Producto producto) {
        // Obtener el índice de la categoría seleccionada en el Spinner
        int index = ObtenerIndiceCategoria(producto.getCategoria());
        spinnerCategorias.setSelection(index);
        etCosto.setText(String.valueOf(producto.getCosto()));
        etPrecioVenta.setText(String.valueOf(producto.getPrecioVenta()));

        // Cargar la imagen utilizando Picasso desde la URL almacenada en Firebase Storage
        Picasso.get().load(producto.getImagenUrl()).into(imaProducto);
    }


    public void mostrarPDF(String rutaPDF) {
        File pdfFile = new File(rutaPDF);

        // Crea un intent para abrir el PDF con el visor de PDF instalado
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(this, "ensayo.app.firebase.crudfirebasemvp.fileprovider", pdfFile);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Necesario para Android 7.0 y superior

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Maneja excepciones si no hay aplicaciones de visor de PDF instaladas
            e.printStackTrace();
            Toast.makeText(this, "No se encontró una aplicación para abrir el PDF", Toast.LENGTH_SHORT).show();
        }
    }


}