package ensayo.app.firebase.crudfirebasemvp.Vista;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ensayo.app.firebase.crudfirebasemvp.MenuAdmins;
import ensayo.app.firebase.crudfirebasemvp.Presentador.RegistroUsuarioContract;
import ensayo.app.firebase.crudfirebasemvp.Presentador.RegistroUsuarioPresenter;
import ensayo.app.firebase.crudfirebasemvp.R;

public class RegistroUsuario extends AppCompatActivity implements RegistroUsuarioContract.View {
    private EditText etNombre;
    private EditText etNombreUsuario;
    private EditText etxtEmail;
    private EditText etxtPass;
    private EditText etxtConfirmarPass;
    Button btnRegistrarUsuario;
    TextView tvAcceder;
    private RegistroUsuarioContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario_admin);

        etNombre = findViewById(R.id.etNombre);
        etNombreUsuario = findViewById(R.id.etNombreUsuario);
        etxtEmail = findViewById(R.id.etxtEmail);
        etxtPass = findViewById(R.id.etxtPassword);
        etxtConfirmarPass = findViewById(R.id.etxtConfirmarPass);
        btnRegistrarUsuario = findViewById(R.id.btnRegistrarUsuario);
        tvAcceder = findViewById(R.id.tvAcceder);

        presenter = new RegistroUsuarioPresenter(this);

        btnRegistrarUsuario.setOnClickListener(v -> {
            String nombreCompleto = etNombre.getText().toString().trim();
            String nombreUsuario = etNombreUsuario.getText().toString().trim();
            String email = etxtEmail.getText().toString().trim();
            String password = etxtPass.getText().toString().trim();
            presenter.registrarUsuario(nombreCompleto, nombreUsuario, email, password);
        });

        tvAcceder.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroUsuario.this, LoginAdmin.class);
            startActivity(intent);
        });

    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showMenuPrincipal() {
        Intent intent = new Intent(RegistroUsuario.this, MenuAdmins.class);
        startActivity(intent);
    }
}

