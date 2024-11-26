package ensayo.app.firebase.crudfirebasemvp.Presentador;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import ensayo.app.firebase.crudfirebasemvp.Modelo.Producto;
import ensayo.app.firebase.crudfirebasemvp.Vista.ProductoFragment;

public class ProductoFragmentPresenter implements ProductoFragmentContract.Presenter {
    ProductoFragment view;
    private DatabaseReference mDatabase;

    public ProductoFragmentPresenter(ProductoFragment view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void listarProductos() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("productos");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Producto> productos = new ArrayList<>();

                for (DataSnapshot productoSnapshot : dataSnapshot.getChildren()) {
                    String nombre = productoSnapshot.child("nombre").getValue(String.class);
                    String imageUrl = productoSnapshot.child("imagenUrl").getValue(String.class);

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
}
