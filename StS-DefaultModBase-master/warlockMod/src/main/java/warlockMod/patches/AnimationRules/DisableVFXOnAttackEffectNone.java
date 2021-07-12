package warlockMod.patches.AnimationRules;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
//import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
//import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
//AbstractDungeon.effectList.add(new FlashAtkImgEffect(__instance.target.hb.cX, __instance.target.hb.cY, __instance.attackEffect, __instance.muteSfx));

@SpirePatch(clz = DamageAction.class, method = "update")
public class DisableVFXOnAttackEffectNone {
        @SpireInsertPatch(loc=76)
        public static void Insert(DamageAction __instance) {
            if(__instance.attackEffect== AbstractGameAction.AttackEffect.NONE){
                AbstractDungeon.effectList.remove(AbstractDungeon.effectList.size()-1);
            }
        }
}
