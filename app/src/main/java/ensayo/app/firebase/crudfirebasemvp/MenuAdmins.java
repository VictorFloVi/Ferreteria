package ensayo.app.firebase.crudfirebasemvp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import ensayo.app.firebase.crudfirebasemvp.Vista.CategoriaAdmin;
import ensayo.app.firebase.crudfirebasemvp.Vista.ClientesAdmin;
import ensayo.app.firebase.crudfirebasemvp.Vista.ProductosAdmin;
import ensayo.app.firebase.crudfirebasemvp.Vista.ProveedoresAdmin;

public class MenuAdmins extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_admins);

        ImageButton btnProductosAdmin = findViewById(R.id.btnProductosAdmin);

        btnProductosAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(MenuAdmins.this, ProductosAdmin.class);
            startActivity(intent);
        });

        ImageButton btnCategoriaAdmin = findViewById(R.id.btnCategoriaAdmin);
        btnCategoriaAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(MenuAdmins.this, CategoriaAdmin.class);
            startActivity(intent);
        });

        ImageButton btnProveedorAdmin = findViewById(R.id.btnProveedorAdmin);
        btnProveedorAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(MenuAdmins.this, ProveedoresAdmin.class);
            startActivity(intent);
        });

        ImageButton btnClienteAdmin = findViewById(R.id.btnClienteAdmin);
        btnClienteAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(MenuAdmins.this, ClientesAdmin.class);
            startActivity(intent);
        });
    }
}
