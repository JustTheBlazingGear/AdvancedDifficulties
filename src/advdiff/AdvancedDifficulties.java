package advdiff;

import advdiff.content.AdvDiffStatusEffects;
import advdiff.content.AdvDiffUnitTypes;
import advdiff.visual.AdvDiffStyles;
import advdiff.visual.BossOutlineRenderer;
import arc.Core;
import mindustry.Vars;
import mindustry.content.Planets;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.mod.*;
import arc.Events;
import mindustry.type.ItemStack;
import mindustry.ui.dialogs.MapPlayDialog;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.power.PowerGenerator;

public class AdvancedDifficulties extends Mod{
    public static PlanetCustomRulesMap rulesMap = new PlanetCustomRulesMap();
    /*
    TODO
    Custom game difficulty select doesn't update the dialog


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
            //Vars.ui.custom = new AdvDiffCustomGameDialog();

            //Your next line is "This code is shit".
            //But bear with me. I know java too little (none) so would absolutely appreciate help or feedback.
            //No idea how to override a vanilla dialog yet.
            //I did override a vanilla dialog fuckeeeeer

            //This mod contains files from 02/09/2024
            //And I'm writing this on 15/09/2026
            //I see it, and it reminds me of how many unfinished projects in various games I have

            //And this is my 2nd ever, if we exclude UberTech, Java project, and the biggest one yet
            //Code is a mess, but I hope next projects will keep getting better
            //Also, I should've learned some Java before diving right in here, should've I?
            //Though first times I touched Java was somewhere in 2024
            Vars.ui.settings.addCategory(Core.bundle.get("advdiff-settings"), Icon.units, t -> {
                t.checkPref("advdiff-forceShowDifficultyButton", true);
                t.checkPref("advdiff-customGameDifficultySelect", true);
                //t.checkPref("advdiff-addNewReconstructors", false);
            });
        });
    }
}