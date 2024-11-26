package ensayo.app.firebase.crudfirebasemvp.Vista;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ensayo.app.firebase.crudfirebasemvp.Modelo.Categoria;
import ensayo.app.firebase.crudfirebasemvp.Presentador.CategoriaAdminContract;
import ensayo.app.firebase.crudfirebasemvp.Presentador.CategoriaAdminPresenter;
import ensayo.app.firebase.crudfirebasemvp.R;

public class CategoriaAdmin extends AppCompatActivity implements CategoriaAdminContract.View {
    private static final String NOMBRE = "nombre";

    private ListView lvlistado;
    private AutoCompleteTextView etNombreCat;
    EditText etDescripcionCat;
    Button btnAgregarCat;
    Button btnConsultarCat;
    Button btnEditarCat;
    Button btnBorrarCat;
    Button btnGenerarPdfCat;
    private ImageView imaCategoria;
    private Uri selectedImageUri;
    private CategoriaAdminContract.Presenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_admin);

        lvlistado = findViewById(R.id.lvListadoCat);
        etNombreCat = findViewById(R.id.etNombreCat);
        etDescripcionCat = findViewById(R.id.etDescripcionCat);
        btnAgregarCat = findViewById(R.id.btnAgregarCat);
        btnConsultarCat = findViewById(R.id.btnConsultarCat);
        btnEditarCat = findViewById(R.id.btnEditarCat);
        btnBorrarCat = findViewById(R.id.btnBorrarCat);
        btnGenerarPdfCat = findViewById(R.id.btnGenerarPdfCat);
        imaCategoria = findViewById(R.id.imaCategoria);
        presenter = new CategoriaAdminPresenter(this);
        presenter.listarCategorias();


        //Detecta cuando se selecciona un elemento de la lista
        lvlistado.setOnItemClickListener((parent, view, position, id) -> {
            Map<String, Object> categoria = (Map<String, Object>) parent.getItemAtPosition(position);
            String nombreCategoria = (String) categoria.get("nombre");

            // Llama al método del presentador para obtener detalles de la categoría
            presenter.clicItemListaCategoria(nombreCategoria);
        });

        //Detecta los cambios en el texto del AutoCompleteTextView
        etNombreCat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String textoBusqueda = s.toString().trim();
                presenter.autocompletarCategoria(textoBusqueda);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        imaCategoria.setOnClickListener(v -> {
            // Lanzar la actividad utilizando el ActivityResultLauncher
            activityResultLauncher.launch("image/*");
        });

        btnAgregarCat.setOnClickListener(v -> {
            String nombre = etNombreCat.getText().toString().trim();
            String descripcion = etDescripcionCat.getText().toString().trim();
            String imageUrl;
            if (selectedImageUri != null) {
                imageUrl = selectedImageUri.toString();
            } else {
                imageUrl = "";
            }
            presenter.agregarCategoria(nombre, descripcion, imageUrl);
            clearEditTextFields();
        });

        btnConsultarCat.setOnClickListener(v -> {
            String nombreCategoria = etNombreCat.getText().toString().trim();
            presenter.consultarCategoria(nombreCategoria);
        });

        btnEditarCat.setOnClickListener(v -> {
            String nombre = etNombreCat.getText().toString().trim();

            String descripcion = etDescripcionCat.getText().toString().trim();

            String imageUrl;
            if (selectedImageUri != null) {
                imageUrl = selectedImageUri.toString();
            } else {
                imageUrl = "";
            }
            presenter.editarCategoria(nombre, descripcion, imageUrl);

        });

        btnBorrarCat.setOnClickListener(v -> {
            String nombre = etNombreCat.getText().toString().trim();
            presenter.borrarCategoria(nombre);
            clearEditTextFields();
        });

        btnGenerarPdfCat.setOnClickListener(v -> {
            // Llama al método para obtener la lista de productos y generar el PDF
            presenter.generarPDFCategorias();
        });

    }

    @Override
    public void showCategorias(List<Categoria> categorias) {
        // Crear un adaptador personalizado para mostrar las categorías en la ListView
        List<Map<String, Object>> categoriasMapList = new ArrayList<>();
        for (Categoria categoria : categorias) {
            Map<String, Object> categoriaMap = new HashMap<>();
            categoriaMap.put("nombre", categoria.getNombre());
            categoriaMap.put("imagenUrl", categoria.getImagenUrl());
            categoriasMapList.add(categoriaMap);
        }

        String[] from = {"nombre", "imagenUrl"};
        int[] to = {R.id.tvNombre, R.id.ivImagen};

        SimpleAdapter adapter = new SimpleAdapter(this, categoriasMapList,
                R.layout.lista_categoria_item, from, to);

        adapter.setViewBinder((view, data, textRepresentation) -> {
            if (view.getId() == R.id.ivImagen) {
                ImageView imageView = (ImageView) view;
                String imageUrl = (String) data;

                Picasso.get().load(imageUrl).into(imageView);

                return true; // Indica que hemos gestionado la vista
            }
            return false;
        });
        lvlistado.setAdapter(adapter);
    }

    //Carga la imagen en el ImageView
    ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            result -> {
                if (result != null) {
                    // El resultado contiene la URI de la imagen seleccionada
                    // Puedes manejar la lógica aquí
                    selectedImageUri = result;

                    // Carga la imagen en el ImageView
                    Picasso.get()
                            .load(selectedImageUri)
                            .fit()
                            .centerCrop()
                            .into(imaCategoria);
                }
            }
    );

    // Método para limpiar los EditText
    public void clearEditTextFields() {
        etNombreCat.setText("");
        etDescripcionCat.setText("");
        imaCategoria.setImageBitmap(null);
    }

    @Override
    public void showSuccessMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }


    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorImage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        imaCategoria.setImageBitmap(null);
    }

    @Override
    public void showDetallesCategoriaSeleccionada(String nombre, String descripcion, String imageUrl) {
        // Mostrar la información en los campos correspondientes
        etNombreCat.setText(nombre);
        etDescripcionCat.setText(descripcion);

        // Cargar la imagen utilizando Picasso desde la URL almacenada en Firebase Storage
        Picasso.get()
                .load(imageUrl)
                .fit()
                .centerCrop()
                .into(imaCategoria);
    }

    @Override
    public void showConsultarCategoria(Categoria categoria) {
        // Aquí puedes mostrar la categoría en tu vista, por ejemplo, actualizar los campos de texto
        etDescripcionCat.setText(categoria.getDescripcion());

        // Cargar la imagen utilizando Picasso desde la URL almacenada en Firebase Storage
        Picasso.get().load(categoria.getImagenUrl()).into(imaCategoria);
    }

    @Override
    public void showCategoriasEncontradasAutocompletado(List<String> categorias) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categorias);
        etNombreCat.setAdapter(adapter);

    }

    public void generarPDFCategorias(List<Map<String, Object>> categorias) {
        try {
            // Ruta donde se guardará el PDF
            String pdfFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/categorias.pdf";

            // Crear el documento PDF
            PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new File(pdfFilePath)));
            Document document = new Document(pdfDocument);

            // Añadir contenido al PDF
            document.add(new Paragraph("Lista de Categorias"));

            for (Map<String, Object> categoria : categorias) {
                String nombre = (String) categoria.get(NOMBRE);
                String descripcion = (String) categoria.get("descripcion");

                //String imageUrl = (String) producto.get(IMAGENURL);

                // Personalizar la información que deseas agregar al PDF
                document.add(new Paragraph("Nombre: " + nombre));
                document.add(new Paragraph("Descripcion: " + descripcion));

                //document.add(new Paragraph("Imagen URL: " + imageUrl));
                document.add(new Paragraph(""));  // Agregar espacio entre productos
            }

            // Cerrar el documento
            document.close();
            mostrarPDF(pdfFilePath);

            // Mostrar un mensaje de éxito
            Toast.makeText(this, "PDF generado correctamente", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al generar el PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarPDF(String rutaPDF) {
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