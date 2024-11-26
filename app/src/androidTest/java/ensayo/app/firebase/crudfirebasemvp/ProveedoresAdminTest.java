package ensayo.app.firebase.crudfirebasemvp;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ensayo.app.firebase.crudfirebasemvp.Vista.ProveedoresAdmin;

@RunWith(AndroidJUnit4.class)
public class ProveedoresAdminTest {

    @Before
    public void setUp() {
        ActivityScenario.launch(ProveedoresAdmin.class);
    }

    @Test
    public void testConsultarProveedor()  {
        // Realiza acciones para buscar y seleccionar un producto (puedes ajustar esto según tu lógica)
        Espresso.onView(ViewMatchers.withId(R.id.etNombreProv))
                .perform(ViewActions.typeText("Ferreteros"),closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.btnConsultarProv))
                .perform(ViewActions.click());
        // Verifica si los campos se llenan con los datos del producto consultado
        onView(withId(R.id.etRUCProv)).check(matches(withText("12345678912")));
        onView(withId(R.id.etTelefonoProv)).check(matches(withText("987654321")));
        onView(withId(R.id.etEmailProv)).check(matches(withText("fer@gmail.com")));
        onView(withId(R.id.etDireccionProv)).check(matches(withText("Av. Los Frutales 157")));
    }

    @Test
    public void testBorrarProveedor() {
        Espresso.onView(ViewMatchers.withId(R.id.etNombreProv))
                .perform(ViewActions.typeText("ddddd"),closeSoftKeyboard());
        onView(withId(R.id.btnBorrarProv)).perform(click());
    }

    @Test
    public void testEditarProveedor() {
        testConsultarProveedor();
        Espresso.onView(ViewMatchers.withId(R.id.etRUCProv))
                .perform(ViewActions.clearText(), ViewActions.typeText("12345678912"),closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.etTelefonoProv))
                .perform(ViewActions.clearText(), ViewActions.typeText("987654321"),closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.etEmailProv))
                .perform(ViewActions.clearText(), ViewActions.typeText("fer@gmail.com"),closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.etDireccionProv))
                .perform(ViewActions.clearText(), ViewActions.typeText("Av. Los Frutales 157"),closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.btnEditarProv))
                .perform(ViewActions.click());
    }

}
