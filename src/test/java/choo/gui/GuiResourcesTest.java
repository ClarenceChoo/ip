package choo.gui;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

class GuiResourcesTest {
    private static final int MINIMUM_AVATAR_SIZE = 256;

    @Test
    void guiResources_packagedForRuntime_areReadable() throws IOException {
        URL stylesheet = Main.class.getResource("/css/main.css");

        assertNotNull(stylesheet, "The GUI stylesheet must be packaged with the application.");
        try (InputStream stylesheetStream = stylesheet.openStream()) {
            assertTrue(stylesheetStream.read() != -1, "The GUI stylesheet must not be empty.");
        }
        assertValidAvatar("/images/ChooAvatar.png");
        assertValidAvatar("/images/UserAvatar.png");
    }

    private static void assertValidAvatar(String resourcePath) throws IOException {
        URL avatarResource = Main.class.getResource(resourcePath);
        assertNotNull(avatarResource, "The avatar must be packaged at " + resourcePath);

        BufferedImage avatar = ImageIO.read(avatarResource);
        assertNotNull(avatar, "The avatar must be a readable image: " + resourcePath);
        assertTrue(avatar.getWidth() >= MINIMUM_AVATAR_SIZE,
                "The avatar must remain sharp when displayed: " + resourcePath);
        assertTrue(avatar.getHeight() >= MINIMUM_AVATAR_SIZE,
                "The avatar must remain sharp when displayed: " + resourcePath);
    }
}
