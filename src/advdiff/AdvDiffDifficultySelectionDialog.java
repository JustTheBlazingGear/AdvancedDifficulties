package advdiff;

import arc.Core;
import arc.func.Boolc;
import arc.func.Boolp;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.game.CampaignRules;
import mindustry.game.Difficulty;
import mindustry.gen.Call;
import mindustry.gen.Tex;
import mindustry.type.Planet;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.CampaignRulesDialog;


public class AdvDiffDifficultySelectionDialog extends CampaignRulesDialog {
    Planet planet;
    Table current;

    public AdvDiffDifficultySelectionDialog() {
        super();

        hidden(() -> {
            if (planet != null) {
                planet.saveRules();

                if (Vars.state.isGame() && Vars.state.isCampaign() && Vars.state.getPlanet() == planet) {
                    planet.campaignRules.apply(planet, Vars.state.rules);
                    Call.setRules(Vars.state.rules);
                }
            }
        });

        onResize(() -> {
            rebuild();
        });
    }

    void rebuild() {

        CampaignRules rules = planet.campaignRules;

        cont.clear();

        cont.top().pane(inner -> {
            inner.top().left().defaults().fillX().left().pad(5);
            current = inner;

            current.table(Tex.button, t -> {
                t.margin(10f);
                var group = new ButtonGroup<>();
                var style = Styles.flatTogglet;

                t.defaults().size(140f, 50f);

                for (Difficulty diff : Difficulty.all) {
                    t.button(diff.localized(), style, () -> {
                                rules.difficulty = diff;
                            }).group(group).checked(b -> rules.difficulty == diff)
                            .tooltip(diff.info());

                    if (Core.graphics.isPortrait() && diff.ordinal() % 2 == 1) {
                        t.row();
                    }
                }
            }).left().fill(false).expand(false, false).row();

            current.table(Tex.button, t -> {
                t.margin(10f);
                var group = new ButtonGroup<>();
                var style = Styles.flatTogglet;

                t.defaults().size(140f, 50f);

                for (AdvDiffDifficulty diff : AdvDiffDifficulty.all) {
                    t.button(diff.localized(), style, () -> {
                                AdvancedDifficulties.difficulty = diff;
                            }).group(group).checked(b -> AdvancedDifficulties.difficulty == diff)
                            .tooltip(diff.info());

                    if (Core.graphics.isPortrait() && diff.ordinal() % 2 == 1) {
                        t.row();
                    }
                }
            }).left().fill(false).expand(false, false).row();

            if(planet.allowSectorInvasion){
                check("@rules.invasions", b -> rules.sectorInvasion = b, () -> rules.sectorInvasion);
            }

            check("@rules.fog", b -> rules.fog = b, () -> rules.fog);
            //check("@rules.hidespawns", b -> rules.hideSpawns = b, () -> rules.hideSpawns);
            check("@rules.randomwaveai", b -> rules.randomWaveAI = b, () -> rules.randomWaveAI);
            check("@rules.pauseDisabled", b -> rules.pauseDisabled = b, () -> rules.pauseDisabled);

            if(planet.showRtsAIRule){
                check("@rules.rtsai.campaign", b -> rules.rtsAI = b, () -> rules.rtsAI);
            }

            if(!planet.clearSectorOnLose){
                check("@rules.clearsectoronloss", b -> rules.clearSectorOnLose = b, () -> rules.clearSectorOnLose);
            }
        }).growY();
    }
    /*
            if (Vars.ui.planet.state.planet == Planets.serpulo) {
        }

        this.cont.table((t) -> {
            t.add("@am-difficulty-select1").top().row();
            t.add("@am-difficulty-select2").top().row();
            t.add("@am-difficulty-select3").top().row();
        }).top().row();

        this.row();

        this.cont.table((t) -> {
            t.button(new TextureRegionDrawable(Core.atlas.find("am-crux-easy")), AMStyles.difficulties, () -> {
                Core.settings.put("@am-serpulo-difficulty", 1);
            }).size(200, 400).center().padRight(50);
            t.button(new TextureRegionDrawable(Core.atlas.find("am-crux-normal")), AMStyles.difficulties, () -> {
                Core.settings.put("@am-serpulo-difficulty", 2);
            }).size(200, 400).center().padRight(50);
            t.button(new TextureRegionDrawable(Core.atlas.find("am-crux-hard")), AMStyles.SerpuloHard, () -> {
                Core.settings.put("@am-serpulo-difficulty", 3);
            }).size(200, 400).center().padRight(50);
            t.button(new TextureRegionDrawable(Core.atlas.find("am-crux-extreme")), AMStyles.SerpuloExtreme, () -> {
                Core.settings.put("@am-serpulo-difficulty", 4);
            }).size(200, 400).center().padRight(50);
            t.button(new TextureRegionDrawable(Core.atlas.find("am-crux-eradication")), AMStyles.SerpuloEradication, () -> {
                Core.settings.put("@am-serpulo-difficulty", 5);
            }).size(200, 400).center().row();
        }).center().row();

        this.row();

        this.cont.table((t) -> {
            t.label(() -> {
                return SerpuloDifficulty == 0 ? "@am-difficulty-unselected" : "@am-difficulty-title" + SerpuloDifficulty;
            }).labelAlign(1).style(Styles.outlineLabel).wrap().center().pad(10).row();
            t.label(() -> {
                return SerpuloDifficulty == 0 ? "" : "@am-serpulo-difficulty" + SerpuloDifficulty;
            });
        }).bottom().row();

        this.buttons.button("@sectors.select", Icon.ok, this::hide).center().size(210F, 64F).padTop(50F);

     */

    public void show(Planet planet) {
        this.planet = planet;

        rebuild();
        show();
    }

    void check(String text, Boolc cons, Boolp prov) {
        check(text, cons, prov, () -> true);
    }

    void check(String text, Boolc cons, Boolp prov, Boolp condition) {
        String infoText = text.substring(1) + ".info";
        var cell = current.check(text, cons).checked(prov.get()).update(a -> a.setDisabled(!condition.get()));
        if (Core.bundle.has(infoText)) {
            cell.tooltip(text + ".info");
        }
        cell.get().left();
        current.row();
    }
}
