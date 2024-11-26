package ensayo.app.firebase.crudfirebasemvp;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ensayo.app.firebase.crudfirebasemvp.Vista.ProductosAdmin;

@RunWith(AndroidJUnit4.class)
public class ProductosAdminTest {
    @Before
    public void setUp() {
        ActivityScenario.launch(ProductosAdmin.class);

    }

    @Test
    public void testGenerarPdf() {
        Espresso.onView(ViewMatchers.withId(R.id.btnGenerarPdf))
                .perform(ViewActions.click());
    }
}

