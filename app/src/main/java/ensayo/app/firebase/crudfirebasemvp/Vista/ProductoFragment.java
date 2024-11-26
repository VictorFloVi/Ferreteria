package ensayo.app.firebase.crudfirebasemvp.Vista;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ensayo.app.firebase.crudfirebasemvp.Modelo.Producto;
import ensayo.app.firebase.crudfirebasemvp.Presentador.ProductoFragmentContract;
import ensayo.app.firebase.crudfirebasemvp.Presentador.ProductoFragmentPresenter;
import ensayo.app.firebase.crudfirebasemvp.R;

public class ProductoFragment extends Fragment {

    private ListView lvlistadop;
    private SimpleAdapter adapter;
    private ProductoFragmentContract.Presenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_producto, container, false);

        lvlistadop = view.findViewById(R.id.lvListadoProd);
        presenter = new ProductoFragmentPresenter(this);
        presenter.listarProductos();

        return view;
    }

    public void showErrorMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void showProductos(List<Producto> productos) {
        // Crear un adaptador personalizado para mostrar las categorías en la ListView
        List<Map<String, Object>> productosMapList = new ArrayList<>();
        for (Producto producto : productos) {
            Map<String, Object> productoMap = new HashMap<>();
            productoMap.put("nombre", producto.getNombre());
            productoMap.put("imagenUrl", producto.getImagenUrl());
            productosMapList.add(productoMap);
        }

        String[] from = {"nombre", "imagenUrl"};
        int[] to = {R.id.tvNombrep, R.id.ivImagenp};

        adapter = new SimpleAdapter(requireContext(), productosMapList,
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
        lvlistadop.setAdapter(adapter);
    }

}