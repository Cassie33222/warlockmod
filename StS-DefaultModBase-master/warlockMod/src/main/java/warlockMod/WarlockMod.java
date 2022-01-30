package warlockMod;

import GifTheSpire.util.GifAnimation;
import basemod.*;
import basemod.eventUtil.AddEventParams;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import warlockMod.cards.AbstractDefaultCard;
import warlockMod.characters.TheWarlock;
import warlockMod.events.IdentityCrisisEvent;
import warlockMod.potions.PlaceholderPotion;
import warlockMod.powers.Voidwalker;
import warlockMod.relics.*;
import warlockMod.util.IDCheckDontTouchPls;
import warlockMod.util.TextureLoader;
import warlockMod.variables.DefaultCustomVariable;
import warlockMod.variables.DefaultSecondMagicNumber;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;


@SpireInitializer
public class WarlockMod implements
        EditCardsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        EditCharactersSubscriber,
        PostInitializeSubscriber {
    // Make sure to implement the subscribers *you* are using (read basemod wiki). Editing cards? EditCardsSubscriber.
    // Making relics? EditRelicsSubscriber. etc., etc., for a full list and how to make your own, visit the basemod wiki.
    public static final Logger logger = LogManager.getLogger(WarlockMod.class.getName());
    private static String modID;

    public static Random rand=new Random(System.nanoTime());

    // Mod-settings settings. This is if you want an on/off savable button
    public static Properties theDefaultDefaultSettings = new Properties();
    public static final String ENABLE_PLACEHOLDER_SETTINGS = "enablePlaceholder";
    public static boolean enablePlaceholder = true; // The boolean we'll be setting on/off (true/false)

    //This is for the in-game mod settings panel.
    private static final String MODNAME = "Warlock Mod";
    private static final String AUTHOR = "Cassie322"; // And pretty soon - You!
    private static final String DESCRIPTION = "Play as a Warlock from World of Warcraft.";
    
    // =============== INPUT TEXTURE LOCATION =================

    public static final Object giflock=new Object();

    // Colors (RGB)
    // Character Color
    public static final Color DEFAULT_GRAY = CardHelper.getColor(42.0f, 21.0f, 63.0f);
    public static final Color DOT_PURPLE = CardHelper.getColor(129, 0, 152);
    public static final Color DOT_ORANGE = CardHelper.getColor(255, 156, 76);
    
    // Potion Colors in RGB
    public static final Color PLACEHOLDER_POTION_LIQUID = CardHelper.getColor(209.0f, 53.0f, 18.0f); // Orange-ish Red
    public static final Color PLACEHOLDER_POTION_HYBRID = CardHelper.getColor(255.0f, 230.0f, 230.0f); // Near White
    public static final Color PLACEHOLDER_POTION_SPOTS = CardHelper.getColor(100.0f, 25.0f, 10.0f); // Super Dark Red/Brown
  
    // Card backgrounds - The actual rectangular card.
    private static final String ATTACK_DEFAULT_GRAY = "warlockModResources/images/512/bg_attack_default_gray.png";
    private static final String SKILL_DEFAULT_GRAY = "warlockModResources/images/512/bg_skill_default_gray.png";
    private static final String POWER_DEFAULT_GRAY = "warlockModResources/images/512/bg_power_default_gray.png";
    
    private static final String ENERGY_ORB_DEFAULT_GRAY = "warlockModResources/images/512/card_default_gray_orb.png";
    private static final String CARD_ENERGY_ORB = "warlockModResources/images/512/card_small_orb.png";
    
    private static final String ATTACK_DEFAULT_GRAY_PORTRAIT = "warlockModResources/images/1024/bg_attack_default_gray.png";
    private static final String SKILL_DEFAULT_GRAY_PORTRAIT = "warlockModResources/images/1024/bg_skill_default_gray.png";
    private static final String POWER_DEFAULT_GRAY_PORTRAIT = "warlockModResources/images/1024/bg_power_default_gray.png";
    private static final String ENERGY_ORB_DEFAULT_GRAY_PORTRAIT = "warlockModResources/images/1024/card_default_gray_orb.png";
    
    // Character assets
    private static final String THE_DEFAULT_BUTTON = "warlockModResources/images/charSelect/DefaultCharacterButton.png";
    private static final String THE_DEFAULT_PORTRAIT = "warlockModResources/images/charSelect/DefaultCharacterPortraitBG.png";
    public static final String THE_DEFAULT_SHOULDER_1 = "warlockModResources/images/char/defaultCharacter/shoulder3.png";
    public static final String THE_DEFAULT_SHOULDER_2 = "warlockModResources/images/char/defaultCharacter/shoulder3.png";
    public static final String THE_DEFAULT_CORPSE = "warlockModResources/images/char/defaultCharacter/corpsenew.png";

    public static GifAnimation warlockgif=
            new GifAnimation("warlockModResources/images/char/defaultCharacter/character.png",
                    5, 4, 0, 0, 0.5f, 0.5f, false);
    /*public static GifAnimation warlockcastingleftgif=
            new GifAnimation("warlockModResources/images/char/defaultCharacter/caststrongleft.png",
                    13, 1, 386, 346, 0.6f, 0.6f, 0, 0, false);
    public static GifAnimation warlockcastingrightgif=
            new GifAnimation("warlockModResources/images/char/defaultCharacter/castweakright2.png",
                    10, 1, 365, 339, 0.6f, 0.6f, 0, 0, false);*/
    public static GifAnimation warlockcastingleftgif=
            new GifAnimation("warlockModResources/images/char/defaultCharacter/caststrongleft.png",
                    13, 1, -85, 5, 0.6f, 0.6f, 0, 0, false);
    public static GifAnimation warlockcastingrightgif=
            new GifAnimation("warlockModResources/images/char/defaultCharacter/castweakright2.png",
                    10, 1, -85-29, 5-7, 0.6f, 0.6f, 0, 0, false);
    /*public static GifAnimation warlockdeathgif=
            new GifAnimation("warlockModResources/images/char/defaultCharacter/death.png",
                    11, 4, 330, 300, 0.6f, 0.6f, 0, 0, false);*/

    public static GifAnimation voidwalkergif=
            new GifAnimation("warlockModResources/images/powers/voidwalkercharacter.png",
                    53, 1, 60, -30, 0.75f, 0.75f, 0, 0,false);

    public static final String warlockselectsound="WARLOCK_SELECT";
    public static final String warlockselectsoundurl="warlockModResources/sounds/characters/warlock/warlockselect.ogg";

    public static final String castingsound="WARLOCK_CASTING";
    public static final String castingsoundurl="warlockModResources/sounds/characters/warlock/casting.ogg";
    public static final String castingfiresound="WARLOCK_CASTING_FIRE";
    public static final String castingfiresoundurl="warlockModResources/sounds/characters/warlock/castingfire.ogg";

    public static final String soulshardsound="SOUL_SHARD";
    public static final String soulshardsoundurl="warlockModResources/sounds/items/soulshard.ogg";

    public static final String shadowimpactsound="SHADOW_IMPACT";
    public static final String shadowimpactsoundurl="warlockModResources/sounds/cards/shadowimpact.ogg";

    public static final String immolatesound="IMMOLATE_IMPACT";
    public static final String immolatesoundurl="warlockModResources/sounds/cards/immolate.ogg";

    public static final String[] summonvoidwalkersounds={"SUMMONVOIDWALKER1", "SUMMONVOIDWALKER2", "SUMMONVOIDWALKER3", "SUMMONVOIDWALKER4"};
    public static final String[] summonvoidwalkersoundurls={
            "warlockModResources/sounds/cards/summonvoidwalker1.ogg",
            "warlockModResources/sounds/cards/summonvoidwalker2.ogg",
            "warlockModResources/sounds/cards/summonvoidwalker3.ogg",
            "warlockModResources/sounds/cards/summonvoidwalker4.ogg"};

    public static final String cursesound="CURSE";
    public static final String cursesoundurl="warlockModResources/sounds/cards/curse.ogg";

    public static final String fearsound="CURSE";
    public static final String fearsoundurl="warlockModResources/sounds/cards/fear.ogg";

    public static final String felarmorsound="FEL_ARMOR";
    public static final String felarmorsoundurl="warlockModResources/sounds/cards/felarmor.ogg";
    public static final String demonarmorsound="DEMON_ARMOR";
    public static final String demonarmorsoundurl="warlockModResources/sounds/cards/demonarmor.ogg";

    public static final String incineratesound="INCINERATE";
    public static final String incineratesoundurl="warlockModResources/sounds/cards/incinerate.ogg";

    public static final String fireblast1sound="FIRE_BLAST_1";
    public static final String fireblast1soundurl="warlockModResources/sounds/cards/fireblast1.ogg";

    public static final String fireblast2sound="FIRE_BLAST_2";
    public static final String fireblast2soundurl="warlockModResources/sounds/cards/fireblast2.ogg";

    public static GifAnimation shadowboltimpactgif=
            new GifAnimation("warlockModResources/images/cards/sbi.png",
                    6, 6, 0, 0, 1f, 1f, 0, 110, true);

    public static GifAnimation drainlifeimpactgif=
            new GifAnimation("warlockModResources/images/cards/drainlifeimpact.png",
                    13, 1, 0, 0, 0.5f, 0.5f, 0, -15, true);

    public static GifAnimation corruptionimpactgif=
            new GifAnimation("warlockModResources/images/cards/corruptionimpact.png",
                    13, 1, 0, 0, 1f, 1f, 0, 0, true);

    public static GifAnimation corruptiontickgif=
            new GifAnimation("warlockModResources/images/powers/corruptiontick.png",
                    17, 2, 0, 0, 1.5f, 1.5f, 0, 50, true);
    public static GifAnimation curseofagonygif=
            new GifAnimation("warlockModResources/images/cards/curseofagonyimpact.png",
                    44, 1, 0, 0, 0.75f, 0.75f, -7, 230, true);
    public static GifAnimation curseofweaknessgif=
            new GifAnimation("warlockModResources/images/cards/curseofweaknessimpact.png",
                    61, 1, 0, 0, 0.75f, 0.75f, -7, 245, true);
    public static GifAnimation immolateimpactgif=
            new GifAnimation("warlockModResources/images/cards/immolateimpact.png",
                    18, 1, 0, 0, 0.75f, 0.75f, 0, 0, true);
    public static GifAnimation incinerategif=
            new GifAnimation("warlockModResources/images/cards/incinerateimpact.png",
                    18, 1, 0, 0, 0.75f, 0.75f, 0, 0, true);
    public static GifAnimation soulfireimpact=
            new GifAnimation("warlockModResources/images/cards/soulfireimpact.png",
                    13, 1, 0, 0, 0.5f, 0.5f, 0, 0, true);
    public static GifAnimation chaosboltimpact=
            new GifAnimation("warlockModResources/images/cards/chaosboltimpact.png",
                    13, 1, 0, 0, 0.5f, 0.5f, 0, 0, true);

    public static GifAnimation immolatetickgif=
            new GifAnimation("warlockModResources/images/powers/immolatetick.png",
                    13, 1, 0, 0, 0.75f, 0.75f, 0, 0, true);

    public static GifAnimation demonarmorgif=
            new GifAnimation("warlockModResources/images/cards/demonarmorapply.png",
                    47, 1, 0, 0, 0.75f, 0.75f, 0, 300, true);
    public static GifAnimation felarmorgif=
            new GifAnimation("warlockModResources/images/cards/felarmorapply.png",
                    47, 1, 0, 0, 0.75f, 0.75f, 0, 300, true);

    public static Texture healthstonepotiontexture;
    //Mod Badge - A small icon that appears in the mod settings menu next to your mod.
    public static final String BADGE_IMAGE = "warlockModResources/images/Badge.png";
    
    // Atlas and JSON files for the Animations
    public static final String THE_DEFAULT_SKELETON_ATLAS = "warlockModResources/images/char/defaultCharacter/skeleton.atlas";
    public static final String THE_DEFAULT_SKELETON_JSON = "warlockModResources/images/char/defaultCharacter/skeleton.json";
    
    // =============== MAKE IMAGE PATHS =================
    
    public static String makeCardPath(String resourcePath) {
        return getModID() + "Resources/images/cards/" + resourcePath;
    }
    
    public static String makeRelicPath(String resourcePath) {
        return getModID() + "Resources/images/relics/" + resourcePath;
    }
    
    public static String makeRelicOutlinePath(String resourcePath) {
        return getModID() + "Resources/images/relics/outline/" + resourcePath;
    }
    
    public static String makeOrbPath(String resourcePath) {
        return getModID() + "Resources/images/orbs/" + resourcePath;
    }
    
    public static String makePowerPath(String resourcePath) {
        return getModID() + "Resources/images/powers/" + resourcePath;
    }
    
    public static String makeEventPath(String resourcePath) {
        return getModID() + "Resources/images/events/" + resourcePath;
    }
    
    // =============== /MAKE IMAGE PATHS/ =================
    
    // =============== /INPUT TEXTURE LOCATION/ =================
    
    
    // =============== SUBSCRIBE, CREATE THE COLOR_GRAY, INITIALIZE =================
    
    public WarlockMod() {
        logger.info("Subscribe to BaseMod hooks");
        
        BaseMod.subscribe(this);
      
        setModID("warlockMod");

        logger.info("Done subscribing");
        
        logger.info("Creating the color " + TheWarlock.Enums.COLOR_GRAY.toString());
        
        BaseMod.addColor(TheWarlock.Enums.COLOR_GRAY, DEFAULT_GRAY, DEFAULT_GRAY, DEFAULT_GRAY,
                DEFAULT_GRAY, DEFAULT_GRAY, DEFAULT_GRAY, DEFAULT_GRAY,
                ATTACK_DEFAULT_GRAY, SKILL_DEFAULT_GRAY, POWER_DEFAULT_GRAY, ENERGY_ORB_DEFAULT_GRAY,
                ATTACK_DEFAULT_GRAY_PORTRAIT, SKILL_DEFAULT_GRAY_PORTRAIT, POWER_DEFAULT_GRAY_PORTRAIT,
                ENERGY_ORB_DEFAULT_GRAY_PORTRAIT, CARD_ENERGY_ORB);
        
        logger.info("Done creating the color");
        
        
        logger.info("Adding mod settings");
        // This loads the mod settings.
        // The actual mod Button is added below in receivePostInitialize()
        theDefaultDefaultSettings.setProperty(ENABLE_PLACEHOLDER_SETTINGS, "FALSE"); // This is the default setting. It's actually set...
        try {
            SpireConfig config = new SpireConfig("defaultMod", "theDefaultConfig", theDefaultDefaultSettings); // ...right here
            // the "fileName" parameter is the name of the file MTS will create where it will save our setting.
            config.load(); // Load the setting and set the boolean to equal it
            enablePlaceholder = config.getBool(ENABLE_PLACEHOLDER_SETTINGS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Done adding mod settings");
        
    }
    
    // ====== NO EDIT AREA ======
    // DON'T TOUCH THIS STUFF. IT IS HERE FOR STANDARDIZATION BETWEEN MODS AND TO ENSURE GOOD CODE PRACTICES.
    // IF YOU MODIFY THIS I WILL HUNT YOU DOWN AND DOWNVOTE YOUR MOD ON WORKSHOP
    
    public static void setModID(String ID) { // DON'T EDIT
        Gson coolG = new Gson(); // EY DON'T EDIT THIS
        //   String IDjson = Gdx.files.internal("IDCheckStringsDONT-EDIT-AT-ALL.json").readString(String.valueOf(StandardCharsets.UTF_8)); // i hate u Gdx.files
        InputStream in = WarlockMod.class.getResourceAsStream("/IDCheckStringsDONT-EDIT-AT-ALL.json"); // DON'T EDIT THIS ETHER
        IDCheckDontTouchPls EXCEPTION_STRINGS = coolG.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), IDCheckDontTouchPls.class); // OR THIS, DON'T EDIT IT
        logger.info("You are attempting to set your mod ID as: " + ID); // NO WHY
        if (ID.equals(EXCEPTION_STRINGS.DEFAULTID)) { // DO *NOT* CHANGE THIS ESPECIALLY, TO EDIT YOUR MOD ID, SCROLL UP JUST A LITTLE, IT'S JUST ABOVE
            throw new RuntimeException(EXCEPTION_STRINGS.EXCEPTION); // THIS ALSO DON'T EDIT
        } else if (ID.equals(EXCEPTION_STRINGS.DEVID)) { // NO
            modID = EXCEPTION_STRINGS.DEFAULTID; // DON'T
        } else { // NO EDIT AREA
            modID = ID; // DON'T WRITE OR CHANGE THINGS HERE NOT EVEN A LITTLE
        } // NO
        logger.info("Success! ID is " + modID); // WHY WOULD U WANT IT NOT TO LOG?? DON'T EDIT THIS.
    } // NO
    
    public static String getModID() { // NO
        return modID; // DOUBLE NO
    } // NU-UH
    
    private static void pathCheck() { // ALSO NO
        Gson coolG = new Gson(); // NOPE DON'T EDIT THIS
        //   String IDjson = Gdx.files.internal("IDCheckStringsDONT-EDIT-AT-ALL.json").readString(String.valueOf(StandardCharsets.UTF_8)); // i still hate u btw Gdx.files
        InputStream in = WarlockMod.class.getResourceAsStream("/IDCheckStringsDONT-EDIT-AT-ALL.json"); // DON'T EDIT THISSSSS
        IDCheckDontTouchPls EXCEPTION_STRINGS = coolG.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), IDCheckDontTouchPls.class); // NAH, NO EDIT
        String packageName = WarlockMod.class.getPackage().getName(); // STILL NO EDIT ZONE
        FileHandle resourcePathExists = Gdx.files.internal(getModID() + "Resources"); // PLEASE DON'T EDIT THINGS HERE, THANKS
        if (!modID.equals(EXCEPTION_STRINGS.DEVID)) { // LEAVE THIS EDIT-LESS
            if (!packageName.equals(getModID())) { // NOT HERE ETHER
                throw new RuntimeException(EXCEPTION_STRINGS.PACKAGE_EXCEPTION + getModID()); // THIS IS A NO-NO
            } // WHY WOULD U EDIT THIS
            if (!resourcePathExists.exists()) { // DON'T CHANGE THIS
                throw new RuntimeException(EXCEPTION_STRINGS.RESOURCE_FOLDER_EXCEPTION + getModID() + "Resources"); // NOT THIS
            }// NO
        }// NO
    }// NO
    
    // ====== YOU CAN EDIT AGAIN ======
    
    
    public static void initialize() {
        logger.info("========================= Initializing Default Mod. Hi. =========================");
        WarlockMod defaultmod = new WarlockMod();
        logger.info("========================= /Default Mod Initialized. Hello World./ =========================");
    }
    
    // ============== /SUBSCRIBE, CREATE THE COLOR_GRAY, INITIALIZE/ =================
    
    
    // =============== LOAD THE CHARACTER =================
    
    @Override
    public void receiveEditCharacters() {
        logger.info("Beginning to edit characters. " + "Add " + TheWarlock.Enums.THE_DEFAULT.toString());
        
        BaseMod.addCharacter(new TheWarlock("the Default", TheWarlock.Enums.THE_DEFAULT),
                THE_DEFAULT_BUTTON, THE_DEFAULT_PORTRAIT, TheWarlock.Enums.THE_DEFAULT);
        
        receiveEditPotions();
        logger.info("Added " + TheWarlock.Enums.THE_DEFAULT.toString());
    }
    
    // =============== /LOAD THE CHARACTER/ =================
    
    
    // =============== POST-INITIALIZE =================
    
    @Override
    public void receivePostInitialize() {
        logger.info("Loading badge image and mod options");
        
        // Load the Mod Badge
        Texture badgeTexture = TextureLoader.getTexture(BADGE_IMAGE);
        
        // Create the Mod Menu
        ModPanel settingsPanel = new ModPanel();
        
        // Create the on/off button:
        ModLabeledToggleButton enableNormalsButton = new ModLabeledToggleButton("This is the text which goes next to the checkbox.",
                350.0f, 700.0f, Settings.CREAM_COLOR, FontHelper.charDescFont, // Position (trial and error it), color, font
                enablePlaceholder, // Boolean it uses
                settingsPanel, // The mod panel in which this button will be in
                (label) -> {}, // thing??????? idk
                (button) -> { // The actual button:
            
            enablePlaceholder = button.enabled; // The boolean true/false will be whether the button is enabled or not
            try {
                // And based on that boolean, set the settings and save them
                SpireConfig config = new SpireConfig("defaultMod", "theDefaultConfig", theDefaultDefaultSettings);
                config.setBool(ENABLE_PLACEHOLDER_SETTINGS, enablePlaceholder);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        settingsPanel.addUIElement(enableNormalsButton); // Add the button to the settings panel. Button is a go.
        
        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);

        
        // =============== EVENTS =================
        // https://github.com/daviscook477/BaseMod/wiki/Custom-Events

        // You can add the event like so:
        // BaseMod.addEvent(IdentityCrisisEvent.ID, IdentityCrisisEvent.class, TheCity.ID);
        // Then, this event will be exclusive to the City (act 2), and will show up for all characters.
        // If you want an event that's present at any part of the game, simply don't include the dungeon ID

        // If you want to have more specific event spawning (e.g. character-specific or so)
        // deffo take a look at that basemod wiki link as well, as it explains things very in-depth
        // btw if you don't provide event type, normal is assumed by default

        // Create a new event builder
        // Since this is a builder these method calls (outside of create()) can be skipped/added as necessary
        AddEventParams eventParams = new AddEventParams.Builder(IdentityCrisisEvent.ID, IdentityCrisisEvent.class) // for this specific event
            .dungeonID(TheCity.ID) // The dungeon (act) this event will appear in
            .playerClass(TheWarlock.Enums.THE_DEFAULT) // Character specific event
            .create();

        // Add the event
        BaseMod.addEvent(eventParams);

        logger.info("Done loading badge Image and mod options");

        // TODO: =============== GIFS =================

        // ADD GIF ANIMATION
        WarlockMod.warlockgif.create();
        warlockgif.setAnimationspeed(0.05f);

        WarlockMod.warlockcastingleftgif.create();
        initializeGif(warlockcastingleftgif, 0.1f);
        warlockcastingleftgif.relativetoplayer=true;

        WarlockMod.warlockcastingrightgif.create();
        initializeGif(warlockcastingrightgif, 0.1f);
        warlockcastingrightgif.relativetoplayer=true;

        WarlockMod.voidwalkergif.create();
        voidwalkergif.setAnimationspeed(0.05f);
        voidwalkergif.addAsBackgroundAnimation();
        voidwalkergif.relativetoplayer=true;

        WarlockMod.shadowboltimpactgif.create();
        initializeGif(shadowboltimpactgif, 0.075f);
        WarlockMod.drainlifeimpactgif.create();
        initializeGif(drainlifeimpactgif, 0.075f);
        WarlockMod.corruptionimpactgif.create();
        initializeGif(corruptionimpactgif, 0.1f);
        WarlockMod.corruptiontickgif.create();
        initializeGif(corruptiontickgif, 0.075f);
        WarlockMod.curseofagonygif.create();
        initializeGif(curseofagonygif, 0.06f);
        WarlockMod.curseofweaknessgif.create();
        initializeGif(curseofweaknessgif, 0.06f);

        WarlockMod.immolateimpactgif.create();
        initializeGif(immolateimpactgif, 0.1f);
        WarlockMod.immolatetickgif.create();
        initializeGif(immolatetickgif, 0.1f);
        WarlockMod.incinerategif.create();
        initializeGif(incinerategif, 0.1f);

        WarlockMod.felarmorgif.create();
        initializeGif(felarmorgif, 0.12f);
        WarlockMod.demonarmorgif.create();
        initializeGif(demonarmorgif, 0.12f);

        WarlockMod.soulfireimpact.create();
        initializeGif(soulfireimpact, 0.075f);
        WarlockMod.chaosboltimpact.create();
        initializeGif(chaosboltimpact, 0.075f);

        //load potion textures
        healthstonepotiontexture=ImageMaster.loadImage("warlockModResources/images/potions/healthstone.png");

        // =============== SOUND EFFECTS =================
        BaseMod.addAudio(warlockselectsound, warlockselectsoundurl);
        BaseMod.addAudio(castingsound, castingsoundurl);
        BaseMod.addAudio(castingfiresound, castingfiresoundurl);
        BaseMod.addAudio(immolatesound, immolatesoundurl);
        BaseMod.addAudio(incineratesound, incineratesoundurl);
        BaseMod.addAudio(soulshardsound, soulshardsoundurl);
        BaseMod.addAudio(shadowimpactsound, shadowimpactsoundurl);
        BaseMod.addAudio(cursesound, cursesoundurl);
        BaseMod.addAudio(fearsound, fearsoundurl);
        BaseMod.addAudio(felarmorsound, felarmorsoundurl);
        BaseMod.addAudio(demonarmorsound, demonarmorsoundurl);
        BaseMod.addAudio(fireblast1sound, fireblast1soundurl);
        BaseMod.addAudio(fireblast2sound, fireblast2soundurl);
        for(int i=0; i<summonvoidwalkersounds.length; i++){
            BaseMod.addAudio(summonvoidwalkersounds[i], summonvoidwalkersoundurls[i]);
        }
        BaseMod.publishAddAudio(CardCrawlGame.sound);
    }
    public void initializeGif(GifAnimation gif, float speed){
        gif.create();
        gif.setAnimationspeed(speed);
        gif.setLoop(false);
        gif.addAsForeGroundAnimation();
        gif.ishidden=true;
    }
    // ================ ADD POTIONS ===================
    
    public void receiveEditPotions() {
        logger.info("Beginning to edit potions");
        
        // Class Specific Potion. If you want your potion to not be class-specific,
        // just remove the player class at the end (in this case the "TheDefaultEnum.THE_DEFAULT".
        // Remember, you can press ctrl+P inside parentheses like addPotions)
        BaseMod.addPotion(PlaceholderPotion.class, PLACEHOLDER_POTION_LIQUID, PLACEHOLDER_POTION_HYBRID, PLACEHOLDER_POTION_SPOTS, PlaceholderPotion.POTION_ID, TheWarlock.Enums.THE_DEFAULT);
        
        logger.info("Done editing potions");
    }
    
    // ================ /ADD POTIONS/ ===================
    
    
    // ================ ADD RELICS ===================
    
    @Override
    public void receiveEditRelics() {
        logger.info("Adding relics");

        // Take a look at https://github.com/daviscook477/BaseMod/wiki/AutoAdd
        // as well as
        // https://github.com/kiooeht/Bard/blob/e023c4089cc347c60331c78c6415f489d19b6eb9/src/main/java/com/evacipated/cardcrawl/mod/bard/BardMod.java#L319
        // for reference as to how to turn this into an "Auto-Add" rather than having to list every relic individually.
        // Of note is that the bard mod uses it's own custom relic class (not dissimilar to our AbstractDefaultCard class for cards) that adds the 'color' field,
        // in order to automatically differentiate which pool to add the relic too.

        // This adds a character specific relic. Only when you play with the mentioned color, will you get this relic.
        BaseMod.addRelicToCustomPool(new PlaceholderRelic(), TheWarlock.Enums.COLOR_GRAY);
        BaseMod.addRelicToCustomPool(new BottledPlaceholderRelic(), TheWarlock.Enums.COLOR_GRAY);
        BaseMod.addRelicToCustomPool(new DefaultClickableRelic(), TheWarlock.Enums.COLOR_GRAY);
        
        // This adds a relic to the Shared pool. Every character can find this relic.
        BaseMod.addRelic(new PlaceholderRelic2(), RelicType.SHARED);

        BaseMod.addRelicToCustomPool(new OrbOfTheSoulEater(), TheWarlock.Enums.COLOR_GRAY);
        
        // Mark relics as seen - makes it visible in the compendium immediately
        // If you don't have this it won't be visible in the compendium until you see them in game
        // (the others are all starters so they're marked as seen in the character file)
        UnlockTracker.markRelicAsSeen(OrbOfTheSoulEater.ID);
        logger.info("Done adding relics!");
    }
    
    // ================ /ADD RELICS/ ===================
    
    
    // ================ ADD CARDS ===================
    
    @Override
    public void receiveEditCards() {
        logger.info("Adding variables");
        //Ignore this
        pathCheck();
        // Add the Custom Dynamic Variables
        logger.info("Add variables");
        // Add the Custom Dynamic variables
        BaseMod.addDynamicVariable(new DefaultCustomVariable());
        BaseMod.addDynamicVariable(new DefaultSecondMagicNumber());
        
        logger.info("Adding cards");

        // This method automatically adds any cards so you don't have to manually load them 1 by 1
        // For more specific info, including how to exclude cards from being added:
        // https://github.com/daviscook477/BaseMod/wiki/AutoAdd

        new AutoAdd("warlockMod") // ${project.artifactId}
            .packageFilter(AbstractDefaultCard.class) // filters to any class in the same package as AbstractDefaultCard, nested packages included
            .setDefaultSeen(true)
            .cards();

        // .setDefaultSeen(true) unlocks the cards
        // This is so that they are all "seen" in the library,
        // for people who like to look at the card list before playing your mod

        logger.info("Done adding cards!");
    }
    
    // ================ /ADD CARDS/ ===================
    
    
    // ================ LOAD THE TEXT ===================
    
    @Override
    public void receiveEditStrings() {
        logger.info("You seeing this?");
        logger.info("Beginning to edit strings for mod with ID: " + getModID());
        
        // CardStrings
        BaseMod.loadCustomStringsFile(CardStrings.class,
                getModID() + "Resources/localization/eng/DefaultMod-Card-Strings.json");
        
        // PowerStrings
        BaseMod.loadCustomStringsFile(PowerStrings.class,
                getModID() + "Resources/localization/eng/DefaultMod-Power-Strings.json");
        
        // RelicStrings
        BaseMod.loadCustomStringsFile(RelicStrings.class,
                getModID() + "Resources/localization/eng/DefaultMod-Relic-Strings.json");
        
        // Event Strings
        BaseMod.loadCustomStringsFile(EventStrings.class,
                getModID() + "Resources/localization/eng/DefaultMod-Event-Strings.json");
        
        // PotionStrings
        BaseMod.loadCustomStringsFile(PotionStrings.class,
                getModID() + "Resources/localization/eng/DefaultMod-Potion-Strings.json");
        
        // CharacterStrings
        BaseMod.loadCustomStringsFile(CharacterStrings.class,
                getModID() + "Resources/localization/eng/DefaultMod-Character-Strings.json");
        
        // OrbStrings
        BaseMod.loadCustomStringsFile(OrbStrings.class,
                getModID() + "Resources/localization/eng/DefaultMod-Orb-Strings.json");
        
        logger.info("Done edittting strings");
    }
    
    // ================ /LOAD THE TEXT/ ===================
    
    // ================ LOAD THE KEYWORDS ===================

    @Override
    public void receiveEditKeywords() {
        // Keywords on cards are supposed to be Capitalized, while in Keyword-String.json they're lowercase
        //
        // Multiword keywords on cards are done With_Underscores
        //
        // If you're using multiword keywords, the first element in your NAMES array in your keywords-strings.json has to be the same as the PROPER_NAME.
        // That is, in Card-Strings.json you would have #yA_Long_Keyword (#y highlights the keyword in yellow).
        // In Keyword-Strings.json you would have PROPER_NAME as A Long Keyword and the first element in NAMES be a long keyword, and the second element be a_long_keyword
        
        Gson gson = new Gson();
        String json = Gdx.files.internal(getModID() + "Resources/localization/eng/DefaultMod-Keyword-Strings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        com.evacipated.cardcrawl.mod.stslib.Keyword[] keywords = gson.fromJson(json, com.evacipated.cardcrawl.mod.stslib.Keyword[].class);
        
        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(getModID().toLowerCase(), keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
                //  getModID().toLowerCase() makes your keyword mod specific (it won't show up in other cards that use that word)
            }
        }
    }
    
    // ================ /LOAD THE KEYWORDS/ ===================    
    
    // this adds "ModName:" before the ID of any card/relic/power etc.
    // in order to avoid conflicts if any other mod uses the same ID.
    public static String makeID(String idText) {
        return getModID() + ":" + idText;
    }

    public static void tick(){
        TheWarlock.tick();
    }

    public static void cleansePower(AbstractCreature m, String power){
        if(m.getPower(power)!=null){
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(m, m, power));
        }
    }
    public static void cleanseDemons(AbstractCreature m){
        String power= Voidwalker.POWER_ID;
        if(m.getPower(power)!=null){
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(m, m, power));
        }
    }
    public static void playRandomSound(String[] sounds){
        CardCrawlGame.sound.play(sounds[rand.nextInt(sounds.length)]);
    }
    public static boolean consumeSpecificOrb(AbstractPlayer p, String orbid){
        //TODO: make sure this is working
            if (!p.orbs.isEmpty()) {
                for(int ss=0; ss<p.orbs.size(); ss++) {
                    if(p.orbs.get(ss).ID!=null&&!p.orbs.get(ss).ID.equalsIgnoreCase(orbid)){
                        continue;
                    }
                    AbstractOrb orbSlot = new EmptyOrbSlot(((AbstractOrb) p.orbs.get(ss)).cX, ((AbstractOrb) p.orbs.get(ss)).cY);

                    int i;
                    for (i = ss+1; i < p.orbs.size(); ++i) {
                        Collections.swap(p.orbs, i, i - 1);
                    }

                    p.orbs.set(p.orbs.size() - 1, orbSlot);

                    for (i = ss; i < p.orbs.size(); ++i) {
                        ((AbstractOrb) p.orbs.get(i)).setSlot(i, p.maxOrbs);
                    }

                    //consumed a soul shard
                    return true;
                }
            }

            return false;
    }
    private static void removeNextOrbExample(AbstractPlayer p, String orbid){
        if (!p.orbs.isEmpty() && !(p.orbs.get(0) instanceof EmptyOrbSlot)) {
            AbstractOrb orbSlot = new EmptyOrbSlot(((AbstractOrb)p.orbs.get(0)).cX, ((AbstractOrb)p.orbs.get(0)).cY);

            int i;
            for(i = 1; i < p.orbs.size(); ++i) {
                Collections.swap(p.orbs, i, i - 1);
            }

            p.orbs.set(p.orbs.size() - 1, orbSlot);

            for(i = 0; i < p.orbs.size(); ++i) {
                ((AbstractOrb)p.orbs.get(i)).setSlot(i, p.maxOrbs);
            }
        }
    }
}
