package warlockMod.characters;

import basemod.abstracts.CustomPlayer;
import basemod.animations.SpineAnimation;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import warlockMod.WarlockMod;
import warlockMod.powers.Voidwalker;
import warlockMod.relics.DefaultClickableRelic;
import warlockMod.relics.OrbOfTheSoulEater;
import warlockMod.relics.PlaceholderRelic;
import warlockMod.relics.PlaceholderRelic2;
import warlockMod.cards.*;

import java.util.ArrayList;

import static warlockMod.characters.TheWarlock.Enums.COLOR_GRAY;

//Wiki-page https://github.com/daviscook477/BaseMod/wiki/Custom-Characters
//and https://github.com/daviscook477/BaseMod/wiki/Migrating-to-5.0
//All text (starting description and loadout, anything labeled TEXT[]) can be found in DefaultMod-character-Strings.json in the resources

public class TheWarlock extends CustomPlayer {
    public static final Logger logger = LogManager.getLogger(WarlockMod.class.getName());
    static PlayerMinion voidwalker;

    // =============== CHARACTER ENUMERATORS =================
    // These are enums for your Characters color (both general color and for the card library) as well as
    // an enum for the name of the player class - IRONCLAD, THE_SILENT, DEFECT, YOUR_CLASS ...
    // These are all necessary for creating a character. If you want to find out where and how exactly they are used
    // in the basegame (for fun and education) Ctrl+click on the PlayerClass, CardColor and/or LibraryType below and go down the
    // Ctrl+click rabbit hole

    public static class Enums {
        @SpireEnum
        public static AbstractPlayer.PlayerClass THE_DEFAULT;
        @SpireEnum(name = "DEFAULT_GRAY_COLOR") // These two HAVE to have the same absolutely identical name.
        public static AbstractCard.CardColor COLOR_GRAY;
        @SpireEnum(name = "DEFAULT_GRAY_COLOR") @SuppressWarnings("unused")
        public static CardLibrary.LibraryType LIBRARY_COLOR;
    }

    // =============== CHARACTER ENUMERATORS  =================


    // =============== BASE STATS =================

    public static final int ENERGY_PER_TURN = 3;
    public static final int STARTING_HP = 80;
    public static final int MAX_HP = 80;
    public static final int STARTING_GOLD = 99;
    public static final int CARD_DRAW = 5;
    public static final int ORB_SLOTS = 3;

    // =============== /BASE STATS/ =================


    // =============== STRINGS =================

    private static final String ID = WarlockMod.makeID("DefaultCharacter");
    private static final CharacterStrings characterStrings = CardCrawlGame.languagePack.getCharacterString(ID);
    private static final String[] NAMES = characterStrings.NAMES;
    private static final String[] TEXT = characterStrings.TEXT;

    // =============== /STRINGS/ =================


    // =============== TEXTURES OF BIG ENERGY ORB ===============

    public static final String[] orbTextures = {
            "warlockModResources/images/char/defaultCharacter/orb/layer1.png",
            "warlockModResources/images/char/defaultCharacter/orb/layer2.png",
            "warlockModResources/images/char/defaultCharacter/orb/layer3.png",
            "warlockModResources/images/char/defaultCharacter/orb/layer4.png",
            "warlockModResources/images/char/defaultCharacter/orb/layer5.png",
            "warlockModResources/images/char/defaultCharacter/orb/layer6.png",
            "warlockModResources/images/char/defaultCharacter/orb/layer1d.png",
            "warlockModResources/images/char/defaultCharacter/orb/layer2d.png",
            "warlockModResources/images/char/defaultCharacter/orb/layer3d.png",
            "warlockModResources/images/char/defaultCharacter/orb/layer4d.png",
            "warlockModResources/images/char/defaultCharacter/orb/layer5d.png",};

    // =============== /TEXTURES OF BIG ENERGY ORB/ ===============

    // =============== CHARACTER CLASS START =================

    public static CharacterAnimations animations;

    public TheWarlock(String name, PlayerClass setClass) {
        /*super(name, setClass, orbTextures,
                "warlockModResources/images/char/defaultCharacter/orb/vfx.png", null,
                new SpriterAnimation(
                        "warlockModResources/images/char/defaultCharacter/Spriter/theDefaultAnimation.scml")
        );*/
        super(name, setClass, orbTextures,
                "warlockModResources/images/char/defaultCharacter/orb/vfx.png", null,
                new SpineAnimation(
                        "warlockModResources/images/char/defaultCharacter/skeleton.atlas",
                        "warlockModResources/images/char/defaultCharacter/skeleton.json",
                        1

                )
        );

        // =============== TEXTURES, ENERGY, LOADOUT =================  

        initializeClass(null, // required call to load textures and setup energy/loadout.
                // I left these in DefaultMod.java (Ctrl+click them to see where they are, Ctrl+hover to see what they read.)
                WarlockMod.THE_DEFAULT_SHOULDER_2, // campfire pose
                WarlockMod.THE_DEFAULT_SHOULDER_1, // another campfire pose
                WarlockMod.THE_DEFAULT_CORPSE, // dead corpse
                getLoadout(), 20.0F, -10.0F, 220.0F, 290.0F, new EnergyManager(ENERGY_PER_TURN)); // energy manager

        // =============== /TEXTURES, ENERGY, LOADOUT/ =================


        // =============== ANIMATIONS =================  

        loadAnimation(
                WarlockMod.THE_DEFAULT_SKELETON_ATLAS,
                WarlockMod.THE_DEFAULT_SKELETON_JSON,
                1.0f);
        AnimationState.TrackEntry e = state.setAnimation(0, "animation", true);
        e.setTime(e.getEndTime() * MathUtils.random());

        // ADD GIF ANIMATION
        WarlockMod.warlockgif.addAsCharacterAnimation(TheWarlock.class.getName());

        //WarlockMod.warlockcastingleftgif.addAsCharacterAnimation(TheWarlock.class.getName());
        //WarlockMod.warlockcastingrightgif.addAsCharacterAnimation(TheWarlock.class.getName());
        //WarlockMod.warlockdeathgif.addAsCharacterAnimation(TheWarlock.class.getName());
        animations=new CharacterAnimations(WarlockMod.warlockgif,  WarlockMod.warlockcastingleftgif
                , WarlockMod.warlockcastingleftgif
                //, WarlockMod.warlockcastingrightgif
        );

        voidwalker=new PlayerMinion(Voidwalker.POWER_ID, WarlockMod.voidwalkergif);
        // =============== /ANIMATIONS/ =================


        // =============== TEXT BUBBLE LOCATION =================

        dialogX = (drawX + 0.0F * Settings.scale); // set location for text bubbles
        dialogY = (drawY + 220.0F * Settings.scale); // you can just copy these values

        // =============== /TEXT BUBBLE LOCATION/ =================

    }

    public static void attack(){
        animations.attack();
    }

    public static void tick(){
        animations.tick();
        voidwalker.tick();
    }

    public static void shadowcastsound(){
        CardCrawlGame.sound.play(WarlockMod.castingsound);
    }
    public static void firecastsound(){
        CardCrawlGame.sound.play(WarlockMod.castingfiresound);
    }

    // =============== /CHARACTER CLASS END/ =================

    // Starting description and loadout
    @Override
    public CharSelectInfo getLoadout() {
        return new CharSelectInfo(NAMES[0], TEXT[0],
                STARTING_HP, MAX_HP, ORB_SLOTS, STARTING_GOLD, CARD_DRAW, this, getStartingRelics(),
                getStartingDeck(), false);
    }

    // Starting Deck
    @Override
    public ArrayList<String> getStartingDeck() {
        ArrayList<String> retVal = new ArrayList<>();

        logger.info("Begin loading starter Deck Strings");

        retVal.add(ShadowBolt.ID);
        retVal.add(ShadowBolt.ID);
        retVal.add(ShadowBolt.ID);
        retVal.add(ShadowBolt.ID);

        retVal.add(DrainLife.ID);
        retVal.add(DrainLife.ID);
        retVal.add(DrainLife.ID);
        retVal.add(CurseOfWeakness.ID);

        retVal.add(Corruption.ID);
        retVal.add(SummonVoidwalker.ID);

        //default cards, don't need em any more
        /*retVal.add(DefaultUncommonAttack.ID);
        retVal.add(DefaultRareAttack.ID);

        retVal.add(DefaultCommonSkill.ID);
        retVal.add(DefaultUncommonSkill.ID);
        retVal.add(DefaultRareSkill.ID);


        retVal.add(DefaultUncommonPower.ID);
        retVal.add(DefaultRarePower.ID);

        retVal.add(DefaultAttackWithVariable.ID);
        retVal.add(DefaultSecondMagicNumberSkill.ID);
        retVal.add(OrbSkill.ID);*/
        return retVal;
    }

    // Starting Relics	
    public ArrayList<String> getStartingRelics() {
        ArrayList<String> retVal = new ArrayList<>();

        retVal.add(OrbOfTheSoulEater.ID);
        //retVal.add(PlaceholderRelic.ID);
        //retVal.add(PlaceholderRelic2.ID);
        //retVal.add(DefaultClickableRelic.ID);

        // Mark relics as seen - makes it visible in the compendium immediately
        // If you don't have this it won't be visible in the compendium until you see them in game
        UnlockTracker.markRelicAsSeen(OrbOfTheSoulEater.ID);
        //UnlockTracker.markRelicAsSeen(PlaceholderRelic.ID);
        //UnlockTracker.markRelicAsSeen(PlaceholderRelic2.ID);
        //UnlockTracker.markRelicAsSeen(DefaultClickableRelic.ID);

        return retVal;
    }

    // character Select screen effect
    @Override
    public void doCharSelectScreenSelectEffect() {
        //CardCrawlGame.sound.playA("ATTACK_DAGGER_1", 1.25f); // Sound Effect
        //BaseMod.addAudio(DefaultMod.warlockselectsound, DefaultMod.warlockselectsoundurl);
        CardCrawlGame.sound.playA(WarlockMod.warlockselectsound, 0.4f); // Sound Effect
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.LOW, ScreenShake.ShakeDur.SHORT,
                false); // Screen Effect
    }

    // character Select on-button-press sound effect
    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        return WarlockMod.warlockselectsound;
    }

    // Should return how much HP your maximum HP reduces by when starting a run at
    // Ascension 14 or higher. (ironclad loses 5, defect and silent lose 4 hp respectively)
    @Override
    public int getAscensionMaxHPLoss() {
        return 0;
    }

    // Should return the card color enum to be associated with your character.
    @Override
    public AbstractCard.CardColor getCardColor() {
        return COLOR_GRAY;
    }

    // Should return a color object to be used to color the trail of moving cards
    @Override
    public Color getCardTrailColor() {
        return WarlockMod.DEFAULT_GRAY;
    }

    // Should return a BitmapFont object that you can use to customize how your
    // energy is displayed from within the energy orb.
    @Override
    public BitmapFont getEnergyNumFont() {
        return FontHelper.energyNumFontRed;
    }

    // Should return class name as it appears in run history screen.
    @Override
    public String getLocalizedCharacterName() {
        return NAMES[0];
    }

    //Which card should be obtainable from the Match and Keep event?
    @Override
    public AbstractCard getStartCardForEvent() {
        return new ShadowBolt();
    }

    // The class name as it appears next to your player name in-game
    @Override
    public String getTitle(AbstractPlayer.PlayerClass playerClass) {
        return NAMES[1];
    }

    // Should return a new instance of your character, sending name as its name parameter.
    @Override
    public AbstractPlayer newInstance() {
        return new TheWarlock(name, chosenClass);
    }

    // Should return a Color object to be used to color the miniature card images in run history.
    @Override
    public Color getCardRenderColor() {
        return WarlockMod.DEFAULT_GRAY;
    }

    // Should return a Color object to be used as screen tint effect when your
    // character attacks the heart.
    @Override
    public Color getSlashAttackColor() {
        return WarlockMod.DEFAULT_GRAY;
    }

    // Should return an AttackEffect array of any size greater than 0. These effects
    // will be played in sequence as your character's finishing combo on the heart.
    // Attack effects are the same as used in DamageAction and the like.
    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[]{
                AbstractGameAction.AttackEffect.POISON,
                AbstractGameAction.AttackEffect.POISON,
                AbstractGameAction.AttackEffect.POISON};
    }

    // Should return a string containing what text is shown when your character is
    // about to attack the heart. For example, the defect is "NL You charge your
    // core to its maximum..."
    @Override
    public String getSpireHeartText() {
        return TEXT[1];
    }

    // The vampire events refer to the base game characters as "brother", "sister",
    // and "broken one" respectively.This method should return a String containing
    // the full text that will be displayed as the first screen of the vampires event.
    @Override
    public String getVampireText() {
        return TEXT[2];
    }

}
