package ensayo.app.firebase.crudfirebasemvp.Presentador;

import android.app.ProgressDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
import ensayo.app.firebase.crudfirebasemvp.Vista.RegistroUsuario;

public class RegistroUsuarioPresenter implements RegistroUsuarioContract.Presenter {
    private RegistroUsuario view;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public RegistroUsuarioPresenter(RegistroUsuario view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    public void registrarUsuario(String nombreCompleto, String nombreUsuario, String email, String password) {
        if (nombreCompleto.isEmpty() || nombreUsuario.isEmpty() || email.isEmpty() || password.isEmpty()) {
            view.showErrorMessage("Todos los campos son obligatorios");
            return; // No realizamos el registro si algún campo está vacío
        }

        ProgressDialog dialog = new ProgressDialog(view);
        dialog.setMessage("Registrando Usuario");
        dialog.setCancelable(false);
        dialog.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                dialog.dismiss();
                Map<String, Object> crearUsuario = new HashMap<>();
                crearUsuario.put("nombre", nombreCompleto);
                crearUsuario.put("username", nombreUsuario);
                crearUsuario.put("email", email);
                mDatabase.child("Usuarios").child(task.getResult().getUser().getUid()).updateChildren(crearUsuario);
                view.showMenuPrincipal();

            } else {
                dialog.dismiss();
                view.showErrorMessage("Los campos son incorrectos.");
            }
        });
    }


}
