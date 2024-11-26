package ensayo.app.firebase.crudfirebasemvp.Vista;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ensayo.app.firebase.crudfirebasemvp.MenuAdmins;
import ensayo.app.firebase.crudfirebasemvp.Presentador.LoginAdminContract;
import ensayo.app.firebase.crudfirebasemvp.Presentador.LoginAdminPresenter;
import ensayo.app.firebase.crudfirebasemvp.R;

public class LoginAdmin extends AppCompatActivity implements LoginAdminContract.View {
    private EditText etEmail;
    private EditText etPassword;
    TextView tvRegistrar;
    Button btnIngresar;
    private LoginAdminContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnIngresar = findViewById(R.id.btnIngresar);
        tvRegistrar = findViewById(R.id.tvRegistrar);
        presenter = new LoginAdminPresenter(this);

        btnIngresar.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            presenter.accederAdmin(email, password);
        });

        tvRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(LoginAdmin.this, RegistroUsuario.class);
            startActivity(intent);
        });

    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showMenuPrincipal() {
        Intent intent = new Intent(LoginAdmin.this, MenuAdmins.class);
        startActivity(intent);
    }

}


