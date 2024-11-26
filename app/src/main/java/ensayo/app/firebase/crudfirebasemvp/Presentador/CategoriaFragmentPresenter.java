package ensayo.app.firebase.crudfirebasemvp.Presentador;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import ensayo.app.firebase.crudfirebasemvp.Vista.CategoriaFragment;
import ensayo.app.firebase.crudfirebasemvp.Modelo.Categoria;

public class CategoriaFragmentPresenter implements CategoriaFragmentContract.Presenter {
    CategoriaFragment view;
    private DatabaseReference mDatabase;

    public CategoriaFragmentPresenter(CategoriaFragment view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }


    public void listarCategorias() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("categorias");

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
}
