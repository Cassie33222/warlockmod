package GifTheSpire;

import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.events.AbstractEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import GifTheSpire.util.GifAnimation;
import java.util.ArrayList;
import java.util.HashMap;

@SpireInitializer
public class GifTheSpireLib{
    public static final Logger logger = LogManager.getLogger(GifTheSpireLib.class.getName());
    private static String modID;
    public static HashMap<String, ArrayList<GifAnimation>> Animations = new HashMap<>();
    //public static GifAnimation FreeRealEstate = new GifAnimation("GifTheSpireResources/images/other/freerealestatepritesheet.png", 5, 17, 0, 0, 2.0F, 2.0F, false );
    public static ArrayList<GifAnimation> TickThis = new ArrayList<>();
    public static AbstractEvent CurrentEvent = null;
    public GifTheSpireLib() {
        //BaseMod.subscribe(this);
       //setModID("GifTheSpireLib");
    }
    @SuppressWarnings("unused")
    public static void initialize() {
        logger.info("Initializing GifMod ö/");
        GifTheSpireLib defaultmod = new GifTheSpireLib();
        logger.info("");
    }

    public static HashMap<String, ArrayList<GifAnimation>> getAnimations()
    {
        return Animations;
    }
}
