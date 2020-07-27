package ly.pp.justpiano3;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class MockitoTest {

    @Mock
    User user;

    @Test
    public void testIsNotNull() {
        assertNotNull(user);
    }
}
