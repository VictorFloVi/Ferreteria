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
import ensayo.app.firebase.crudfirebasemvp.Modelo.Categoria;
import ensayo.app.firebase.crudfirebasemvp.Presentador.CategoriaFragmentContract;
import ensayo.app.firebase.crudfirebasemvp.Presentador.CategoriaFragmentPresenter;
import ensayo.app.firebase.crudfirebasemvp.R;

public class CategoriaFragment extends Fragment {
    private ListView lvlistado;
    private SimpleAdapter adapter;
    private CategoriaFragmentContract.Presenter presenter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categoria, container, false);

        lvlistado = view.findViewById(R.id.lvListadoCat);
        presenter = new CategoriaFragmentPresenter(this);
        presenter.listarCategorias();
        return view;
    }

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

        adapter = new SimpleAdapter(requireContext(), categoriasMapList,
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

    public void showErrorMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}