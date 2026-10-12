package advdiff;

import advdiff.content.AdvDiffStatusEffects;
import advdiff.content.AdvDiffUnitTypes;
import advdiff.visual.AdvDiffStyles;
import advdiff.visual.BossOutlineRenderer;
import arc.Core;
import arc.func.Boolc;
import arc.scene.event.VisibilityListener;
import arc.scene.ui.Button;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Scl;
import arc.util.Log;
import arc.util.OS;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Planets;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.mod.*;
import arc.Events;
import mindustry.type.ItemStack;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.MapPlayDialog;
import mindustry.ui.dialogs.SettingsMenuDialog;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.power.PowerGenerator;

import static arc.Core.settings;

public class AdvancedDifficulties extends Mod{
    public static PlanetCustomRulesMap rulesMap = new PlanetCustomRulesMap();
    boolean overrideOtherDialogReplacementsOld = settings.getBool("advdiff-overrideOtherDialogReplacements");
    /*
    TODO


    T0 units
    Check how NH Jump Gates work
    Would be cool to add every single custom game rule in here
     */

    public void loadContent() {
        AdvDiffStatusEffects.load();
        AdvDiffUnitTypes.load();
    }

    public AdvancedDifficulties() {
        BossOutlineRenderer.init();

        Events.run(EventType.ClientLoadEvent.class, () -> {
            //Log.info("Shit's about to load");
            rulesMap.load();
            AdvDiffStyles.load();
            AdvDiffCampaignRules campaignRules = new AdvDiffCampaignRules(Planets.sun);
            campaignRules.applyEvents();
            Vars.ui.campaignRules = new AdvDiffDifficultySelectionDialog();
            if (overrideOtherDialogReplacementsOld) Time.run(1f, () -> {
                Vars.ui.campaignRules = new AdvDiffDifficultySelectionDialog();
            });
            //Vars.ui.custom = new AdvDiffCustomGameDialog();

            //Your next line is "This code is shit".
            //But bear with me. I know java too little (none) so would absolutely appreciate help or feedback.
            //No idea how to override a vanilla dialog yet.
            //I did override a vanilla dialog fuckeeeeer

            //This mod contains files from 02/09/2024
            //And I'm writing this on 15/09/2026
            //I see it, and it reminds me of how many unfinished projects in various games I have

            //And this is my 2nd ever, if I exclude UberTech, Java project, and the biggest one yet
            //Code is a mess, but I hope next projects will keep getting better
            //Also, I should've learned some Java before diving right in here, should've I?
            //Though first times I touched Java was somewhere in 2024
            Vars.ui.settings.addCategory("@advdiff-settings", Icon.units, t -> {
                t.checkPref("advdiff-forceShowDifficultyButton", true);
                t.checkPref("advdiff-customGameDifficultySelect", true);
                //t.checkPref("advdiff-addNewReconstructors", false);
                t.checkPref("advdiff-overrideOtherDialogReplacements", true);
            });
            Events.run(EventType.Trigger.universeDrawEnd, () -> {
                if ((settings.getBool("advdiff-overrideOtherDialogReplacements") && !overrideOtherDialogReplacementsOld)) {
                    //Log.info("Diolch update");
                    Vars.ui.campaignRules = new AdvDiffDifficultySelectionDialog();
                    overrideOtherDialogReplacementsOld = true;
                }
            });
        });
    }
}