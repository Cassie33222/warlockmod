package warlockMod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.Corruption;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.DeadBranch;
import warlockMod.WarlockMod;
import warlockMod.powers.Immolate;
import warlockMod.powers.Spellpower;
import warlockMod.util.TextureLoader;

public class DemonicKnowledge extends CustomRelic {

    //At the start of combat, gain 1 Spellpower.

    // ID, images, text.
    public static final String ID = WarlockMod.makeID("DemonicKnowledge");

    private static final Texture IMG = TextureLoader.getTexture(WarlockMod.makeRelicPath("demonicknowledge.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(WarlockMod.makeRelicOutlinePath("soulstone.png"));

    public DemonicKnowledge() {
        super(ID, IMG, OUTLINE, RelicTier.BOSS, LandingSound.MAGICAL);
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.POWER) {
            this.flash();
            addSpellpower();
        }
    }

    public void addSpellpower(){
        AbstractPlayer p=AbstractDungeon.player;
        AbstractPower power=p.getPower(Spellpower.POWER_ID);
        if(power!=null&&power.amount>0){
            power.stackPower(1);
            (power).updateDescription();
        }
        else{
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                    new Spellpower(AbstractDungeon.player, AbstractDungeon.player, 1), 0));
        }
    }

    // Description
    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}
