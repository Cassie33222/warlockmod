//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package GifTheSpire.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureLoader {
    private static HashMap<String, Texture> textures = new HashMap();
    public static final Logger logger = LogManager.getLogger(TextureLoader.class.getName());

    public TextureLoader() {
    }

    public static Texture getTexture(String textureString) {
        if (textures.get(textureString) == null) {
            try {
                loadTexture(textureString);
            } catch (GdxRuntimeException var2) {
                logger.error("Could not find texture: " + textureString);
                return getTexture("GifTheSpireResources/images/ui/missing_texture.png");
            }
        }

        return (Texture)textures.get(textureString);
    }

    private static void loadTexture(String textureString) throws GdxRuntimeException {
        logger.info("GifTheSpireLib | Loading Texture: " + textureString);
        Texture texture = new Texture(textureString);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        textures.put(textureString, texture);
    }
}
