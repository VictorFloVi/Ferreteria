package ensayo.app.firebase.crudfirebasemvp.Vista;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ensayo.app.firebase.crudfirebasemvp.Modelo.Proveedor;
import ensayo.app.firebase.crudfirebasemvp.Presentador.ProveedoresAdminContract;
import ensayo.app.firebase.crudfirebasemvp.Presentador.ProveedoresAdminPresenter;
import ensayo.app.firebase.crudfirebasemvp.R;

public class ProveedoresAdmin extends AppCompatActivity implements ProveedoresAdminContract.View {

    private static final String NOMBRE = "nombre";
    private ListView lvlistado;
    private AutoCompleteTextView etNombreProv;
    EditText etRUCProv;
    EditText etTelefonoProv;
    EditText etEmailProv;
    EditText etDireccionProv;
    Button btnAgregarProv;
    Button btnConsultarProv;
    Button btnEditarProv;
    Button btnBorrarProv;

    private ProveedoresAdminContract.Presenter presenter;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proveedores_admin);

        lvlistado = findViewById(R.id.lvListadoProv);
        etNombreProv = findViewById(R.id.etNombreProv);
        etRUCProv = findViewById(R.id.etRUCProv);
        etTelefonoProv = findViewById(R.id.etTelefonoProv);
        etEmailProv = findViewById(R.id.etEmailProv);
        etDireccionProv = findViewById(R.id.etDireccionProv);
        btnAgregarProv = findViewById(R.id.btnAgregarProv);
        btnConsultarProv = findViewById(R.id.btnConsultarProv);
        btnEditarProv = findViewById(R.id.btnEditarProv);
        btnBorrarProv = findViewById(R.id.btnBorrarProv);

        presenter = new ProveedoresAdminPresenter(this);
        presenter.listarProveedores();

        //Detecta cuando se selecciona un elemento de la lista
        lvlistado.setOnItemClickListener((parent, view, position, id) -> {
            Map<String, Object> proveedor = (Map<String, Object>) parent.getItemAtPosition(position);
            String nombreProveedor = (String) proveedor.get(NOMBRE);

            // Llama al método del presentador para obtener detalles de la categoría
            presenter.clicItemListaProveedor(nombreProveedor);
        });

        //Detecta los cambios en el texto del AutoCompleteTextView
        etNombreProv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String textoBusqueda = s.toString().trim();
                presenter.autocompletarProveedor(textoBusqueda);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnAgregarProv.setOnClickListener(v -> {

            String nombre = etNombreProv.getText().toString().trim();
            String ruc = etRUCProv.getText().toString().trim();
            String telefono = etTelefonoProv.getText().toString().trim();
            String email = etEmailProv.getText().toString().trim();
            String direccion = etDireccionProv.getText().toString().trim();

            presenter.agregarProveedor(nombre, ruc, telefono, email, direccion);
        });

        btnConsultarProv.setOnClickListener(v -> {
            String nombreProveedor = etNombreProv.getText().toString().trim();
            presenter.consultarProveedor(nombreProveedor);
        });

        btnEditarProv.setOnClickListener(v -> {
            String nombre = etNombreProv.getText().toString().trim();
            String ruc = etRUCProv.getText().toString().trim();
            String telefono = etTelefonoProv.getText().toString().trim();
            String email = etEmailProv.getText().toString().trim();
            String direccion = etDireccionProv.getText().toString().trim();

            presenter.editarProveedor(nombre, ruc, telefono, email, direccion);

        });

        btnBorrarProv.setOnClickListener(v -> {
            String nombre = etNombreProv.getText().toString().trim();
            presenter.borrarProveedor(nombre);
            clearEditTextFields();
        });
    }


    @Override
    public void showProveedoresEncontradosAutocompletado(List<String> proveedores) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, proveedores);
        etNombreProv.setAdapter(adapter);

    }

    public void showProveedores(List<Proveedor> proveedores) {
        // Crear un adaptador personalizado para mostrar los productos en la ListView
        List<Map<String, Object>> proveedoresMapList = new ArrayList<>();
        for (Proveedor proveedor : proveedores) {
            Map<String, Object> proveedorMap = new HashMap<>();
            proveedorMap.put(NOMBRE, proveedor.getNombre());
            proveedoresMapList.add(proveedorMap);
        }

        String[] from = {NOMBRE};
        int[] to = {R.id.tvNombrep};

        adapter = new SimpleAdapter(this, proveedoresMapList,
                R.layout.lista_proveedor_item, from, to);


        lvlistado.setAdapter(adapter);
    }

    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showSuccessMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        clearEditTextFields();
    }

    public void clearEditTextFields() {
        etNombreProv.setText("");
        etRUCProv.setText("");
        etTelefonoProv.setText("");
        etEmailProv.setText("");
        etDireccionProv.setText("");
    }


    public void showDetallesProveedorSeleccionado(String nombre, String rucProveedor, String telefonoProveedor, String emailProveedor, String direccionProveedor) {

        // Mostrar la información en los campos correspondientes
        etNombreProv.setText(nombre);
        etRUCProv.setText(rucProveedor);
        etTelefonoProv.setText(telefonoProveedor);
        etEmailProv.setText(emailProveedor);
        etDireccionProv.setText(direccionProveedor);

    }

    public void showConsultarProveedor(Proveedor proveedor) {

        etRUCProv.setText(String.valueOf(proveedor.getRuc()));
        etTelefonoProv.setText(String.valueOf(proveedor.getTelefono()));
        etEmailProv.setText(String.valueOf(proveedor.getEmail()));
        etDireccionProv.setText(String.valueOf(proveedor.getDireccion()));
    }
}