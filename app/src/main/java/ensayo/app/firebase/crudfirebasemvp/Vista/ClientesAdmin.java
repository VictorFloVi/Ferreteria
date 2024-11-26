package ensayo.app.firebase.crudfirebasemvp.Vista;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ensayo.app.firebase.crudfirebasemvp.Modelo.Cliente;
import ensayo.app.firebase.crudfirebasemvp.Modelo.Proveedor;
import ensayo.app.firebase.crudfirebasemvp.Presentador.ClienteAdminContract;
import ensayo.app.firebase.crudfirebasemvp.Presentador.ClienteAdminPresenter;
import ensayo.app.firebase.crudfirebasemvp.Presentador.ProveedoresAdminPresenter;
import ensayo.app.firebase.crudfirebasemvp.R;

public class ClientesAdmin extends AppCompatActivity implements ClienteAdminContract.View{
    private static final String NOMBRE = "nombre";
    private ListView lvlistado;
    private AutoCompleteTextView etNombreCli;
    EditText etApellidosCli;
    EditText etDNICli;
    EditText etTelefonoCli;
    EditText etEmailCli;
    Button btnAgregarCli;
    Button btnConsultarCli;
    Button btnEditarCli;
    Button btnBorrarCli;

    private ClienteAdminContract.Presenter presenter;
    private SimpleAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes_admin);

        lvlistado = findViewById(R.id.lvListadoCli);
        etNombreCli = findViewById(R.id.etNombreCli);
        etApellidosCli = findViewById(R.id.etApellidosCli);
        etDNICli = findViewById(R.id.etDNICli);
        etTelefonoCli = findViewById(R.id.etTelefonoCli);
        etEmailCli = findViewById(R.id.etEmailCli);
        btnAgregarCli = findViewById(R.id.btnAgregarCli);
        btnConsultarCli = findViewById(R.id.btnConsultarCli);
        btnEditarCli = findViewById(R.id.btnEditarCli);
        btnBorrarCli = findViewById(R.id.btnBorrarCli);

        presenter = new ClienteAdminPresenter(this);
        presenter.listarClientes();

        //Detecta cuando se selecciona un elemento de la lista
        lvlistado.setOnItemClickListener((parent, view, position, id) -> {
            Map<String, Object> cliente = (Map<String, Object>) parent.getItemAtPosition(position);
            String nombreCliente = (String) cliente.get(NOMBRE);

            // Llama al método del presentador para obtener detalles de la categoría
            presenter.clicItemListaCliente(nombreCliente);
        });

        //Detecta los cambios en el texto del AutoCompleteTextView
        etNombreCli.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String textoBusqueda = s.toString().trim();
                presenter.autocompletarCliente(textoBusqueda);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnAgregarCli.setOnClickListener(v -> {

            String nombre = etNombreCli.getText().toString().trim();
            String apellido = etApellidosCli.getText().toString().trim();
            String dni = etDNICli.getText().toString().trim();
            String telefono = etTelefonoCli.getText().toString().trim();
            String email = etEmailCli.getText().toString().trim();

            presenter.agregarCliente(nombre, apellido, dni, telefono, email);
        });

        btnConsultarCli.setOnClickListener(v -> {
            String nombreCliente = etNombreCli.getText().toString().trim();
            presenter.consultarCliente(nombreCliente);
        });

        btnEditarCli.setOnClickListener(v -> {
            String nombre = etNombreCli.getText().toString().trim();
            String apellido = etApellidosCli.getText().toString().trim();
            String dni = etDNICli.getText().toString().trim();
            String telefono = etTelefonoCli.getText().toString().trim();
            String email = etEmailCli.getText().toString().trim();

            presenter.editarCliente(nombre, apellido, dni, telefono, email);

        });

        btnBorrarCli.setOnClickListener(v -> {
            String nombre = etNombreCli.getText().toString().trim();
            presenter.borrarCliente(nombre);
            clearEditTextFields();
        });

    }

    public void showClientes(List<Cliente> clientes) {
        // Crear un adaptador personalizado para mostrar los clientes en la ListView
        List<Map<String, Object>> clientesMapList = new ArrayList<>();
        for (Cliente cliente : clientes) {
            Map<String, Object> clienteMap = new HashMap<>();
            clienteMap.put(NOMBRE, cliente.getNombre());
            clientesMapList.add(clienteMap);
        }

        String[] from = {NOMBRE};
        int[] to = {R.id.tvNombrep};

        adapter = new SimpleAdapter(this, clientesMapList,
                R.layout.lista_cliente_item, from, to);


        lvlistado.setAdapter(adapter);
    }

    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showDetallesClienteSeleccionado(String nombre, String apellidoCliente, String dniCliente, String telefonoCliente, String emailCliente) {
        // Mostrar la información en los campos correspondientes
        etNombreCli.setText(nombre);
        etApellidosCli.setText(apellidoCliente);
        etDNICli.setText(dniCliente);
        etTelefonoCli.setText(telefonoCliente);
        etEmailCli.setText(emailCliente);
    }

    public void showClientesEncontradosAutocompletado(List<String> clientes) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, clientes);
        etNombreCli.setAdapter(adapter);
    }

    public void showSuccessMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        clearEditTextFields();
    }

    public void clearEditTextFields() {
        etNombreCli.setText("");
        etApellidosCli.setText("");
        etDNICli.setText("");
        etTelefonoCli.setText("");
        etEmailCli.setText("");
    }

    public void showConsultarCliente(Cliente cliente) {
        etApellidosCli.setText(String.valueOf(cliente.getApellido()));
        etDNICli.setText(String.valueOf(cliente.getDni()));
        etTelefonoCli.setText(String.valueOf(cliente.getTelefono()));
        etEmailCli.setText(String.valueOf(cliente.getEmail()));
    }
}