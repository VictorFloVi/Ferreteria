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

import ensayo.app.firebase.crudfirebasemvp.Vista.ClientesAdmin;


@RunWith(AndroidJUnit4.class)
public class ClientesAdminTest {

    @Before
    public void setUp() {
        ActivityScenario.launch(ClientesAdmin.class);

    }

    @Test
    public void testConsultarCliente()  {
        // Realiza acciones para buscar y seleccionar un producto (puedes ajustar esto según tu lógica)
        Espresso.onView(ViewMatchers.withId(R.id.etNombreCli))
                .perform(ViewActions.typeText("Xavier"),closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.btnConsultarCli))
                .perform(ViewActions.click());
        // Verifica si los campos se llenan con los datos del producto consultado
        onView(withId(R.id.etApellidosCli)).check(matches(withText("Vasquez")));
        onView(withId(R.id.etDNICli)).check(matches(withText("12345678")));
        onView(withId(R.id.etTelefonoCli)).check(matches(withText("987654321")));
        onView(withId(R.id.etEmailCli)).check(matches(withText("xvasquez@gmail.com")));
    }

    @Test
    public void testBorrarCliente() {
        Espresso.onView(ViewMatchers.withId(R.id.etNombreCli))
                .perform(ViewActions.typeText("Lucas"),closeSoftKeyboard());
        onView(withId(R.id.btnBorrarCli)).perform(click());
    }

    @Test
    public void testEditarCliente() {
        testConsultarCliente();
        Espresso.onView(ViewMatchers.withId(R.id.etApellidosCli))
                .perform(ViewActions.clearText(), ViewActions.typeText("Vasquez"),closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.etDNICli))
                .perform(ViewActions.clearText(), ViewActions.typeText("12345678"),closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.etTelefonoCli))
                .perform(ViewActions.clearText(), ViewActions.typeText("987654321"),closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.etEmailCli))
                .perform(ViewActions.clearText(), ViewActions.typeText("xvasquez@gmail.com"),closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.btnEditarCli))
                .perform(ViewActions.click());
    }
}
