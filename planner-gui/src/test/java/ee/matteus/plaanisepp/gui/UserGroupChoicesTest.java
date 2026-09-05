package ee.matteus.plaanisepp.gui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserGroupChoicesTest {
    @Test void internalCableGroupIsNotOfferedAsAnObjectGroup() {
        assertFalse(PlaaniseppApp.isUserGroupName("__plaanisepp_cables__"));
        assertFalse(PlaaniseppApp.isUserGroupName("plaanisepp_cables"));
        assertTrue(PlaaniseppApp.isUserGroupName("Kaablid"));
        assertTrue(PlaaniseppApp.isUserGroupName("Korraldus"));
    }
}
