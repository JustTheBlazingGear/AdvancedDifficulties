package advdiff;

import advdiff.visual.AdvDiffStyles;
import arc.Core;
import arc.Events;
import arc.func.*;
import arc.scene.event.Touchable;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Planets;
import mindustry.core.GameState;
import mindustry.game.*;
import mindustry.gen.Call;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.type.Planet;
import mindustry.ui.dialogs.CampaignRulesDialog;

import static advdiff.AdvancedDifficulties.rulesMap;
import static arc.Core.settings;
import static mindustry.Vars.mobile;
import static mindustry.Vars.ui;

public class AdvDiffDifficultySelectionDialog extends CampaignRulesDialog {
    Planet planet;
    Table current;
    AdvDiffCampaignRules customRule;
    AdvDiffDifficultyExtrasDialog extrasDialog = new AdvDiffDifficultyExtrasDialog();
    String waveTeam;
    int loop = 0;

    public AdvDiffDifficultySelectionDialog() {
        super();

        Events.on(EventType.SaveLoadEvent.class, e -> {
            if(Vars.state.isGame() && Vars.state.isCampaign()){
                //rulesMap.get(Vars.state.getPlanet()).applyEvents();
                Call.setRules(Vars.state.rules);
            }
        });
        Events.on(EventType.SectorLaunchEvent.class, e -> {
            if(Vars.state.isCampaign()){
                rulesMap.get(e.sector.planet).apply(e.sector.planet, Vars.state.rules);
                Call.setRules(Vars.state.rules);
            }
        });
        hidden(() -> {
            if (this.planet != null && this.planet != Planets.sun) {
                this.planet.saveRules();
                rulesMap.save(planet, customRule);
                rulesMap.get(planet).applyPlanet(planet);
            }
        });

        onResize(this::rebuild);
    }
    @Override
    protected void onResize(Runnable run) {
        Events.on(EventType.ResizeEvent.class, (e) -> {
            if (isShown() && Core.scene.getDialog() == this && !Core.input.isShowingTextInput()) {
                rebuild();
            }
        });
    }
    public void show(Planet planet) {
        this.planet = planet;
        //planet.campaignRules.difficulty = Difficulty.normal;
        customRule = rulesMap.get(planet);
        rebuild();
        show();
    }

    void rebuild() {
        //CampaignRules rules = planet.campaignRules;
        Rules rules3 = new Rules();
        planet.ruleSetter.get(rules3);
        waveTeam = rules3.waveTeam.name;
        //if (Core.atlas.find("advdiff-" + waveTeam + "-normal") == Core.atlas.find("error")) waveTeam = Team.crux.name;
        if (!Core.atlas.isFound(Core.atlas.find("advdiff-" + waveTeam + "-normal"))) waveTeam = Team.crux.name;

        buttons.clear();
        cont.clear();

        cont.add().padTop(200);

        cont.center().pane(inner -> {
            inner.center().defaults().fillX().pad(15);
            current = inner;

            current.table(t -> {
                t.margin(10f);
                var group = new ButtonGroup<>();

                t.defaults().size(200, 400);
                if (mobile || Core.graphics.getAspect() < 16f/9f) t.defaults().size(150, 300);
                for (AdvDiffDifficulty diff : AdvDiffDifficulty.all) {
                    if (diff != AdvDiffDifficulty.custom) {
                        int padR = 15;
                        int padL = 15;
                        int padT = 0;
                        //Whatever, imma hardcode
                        //No, I don't want to extract shit, Intellij
                        var style = AdvDiffStyles.difficulties;
                        if (waveTeam.equals("crux") && diff == AdvDiffDifficulty.hard) style = AdvDiffStyles.cruxhard;
                        if (waveTeam.equals("crux") && diff == AdvDiffDifficulty.eradication) style = AdvDiffStyles.cruxeradication;
                        if (waveTeam.equals("crux") && diff == AdvDiffDifficulty.unreasonable) style = AdvDiffStyles.cruxunreasonable;

                        if (waveTeam.equals("malis") && diff == AdvDiffDifficulty.hard) style = AdvDiffStyles.malishard;
                        if (waveTeam.equals("malis") && diff == AdvDiffDifficulty.eradication) style = AdvDiffStyles.maliseradication;
                        if (waveTeam.equals("malis") && diff == AdvDiffDifficulty.unreasonable) style = AdvDiffStyles.malisunreasonable;

                        if (diff == AdvDiffDifficulty.casual) padL = 100;
                        if (diff == AdvDiffDifficulty.unreasonable) padR = 100;
                        if (Core.graphics.isPortrait()) {
                            padL = 10;
                            padR = 10;
                            padT = 20;
                        }
                        var icon = new TextureRegionDrawable(Core.atlas.find("advdiff-" + waveTeam + "-" + diff));
                        float iconScale;
                        if (mobile || Core.graphics.getAspect() < 16f/9f) {iconScale = 0.75f;} else {iconScale = 1f;}
                        t.button(icon, style, () -> {
                            customRule.set(diff);
                            //if (mobile) ui.showInfo(diff.info());
                            rebuild();
                        }).padLeft(padL).padRight(padR).padTop(padT).group(group).checked(b -> customRule.advDiff == diff).tooltip(diff.info(), true)
                                .with(i -> i.getImage().touchable = Touchable.disabled)
                                .with(i -> i.getImage().scaleX = iconScale).with(i -> i.getImage().scaleY = iconScale);
                    }
                    if (Core.graphics.isPortrait() && diff.ordinal() % 3 == 2) {
                        t.row();
                    }
                }
            }).center().fill(false).expand(false, false).row();
        });

        current.table(Tex.button, t -> {
            loop = 0;
            if (planet.allowSectorInvasion) check("@rules.invasions", b -> customRule.sectorInvasion = b, () -> customRule.sectorInvasion, t);
            check("@rules.fog", b -> customRule.fog = b, () -> customRule.fog, t);
            check("@rules.hidespawns", b -> customRule.hideSpawns = b, () -> customRule.hideSpawns, t);
            check("@rules.randomwaveai", b -> customRule.randomWaveAI = b, () -> customRule.randomWaveAI, t);
            check("@rules.pauseDisabled", b -> customRule.pauseDisabled = b, () -> customRule.pauseDisabled, t);
            if (planet.showRtsAIRule) check("@rules.rtsai.campaign", b -> customRule.rtsAI = b, () -> customRule.rtsAI, t);
            if (!planet.clearSectorOnLose) check("@rules.clearsectoronloss", b -> customRule.clearSectorOnLose = b, () -> customRule.clearSectorOnLose, t);

            t.row();

        }).center().bottom().fill(false).expand(false, false);
        buttons.button("@back", Icon.left, this::hide).size(210, 64.0F).center().bottom();
        addCloseListener();
        buttons.button("@extras",
                Icon.book,() -> {
                    extrasDialog.show(planet);
                    this.hide();
                }).size(210, 64).center().bottom();

    }

    void check(String text, Boolc cons, Boolp prov, Table table) {
        check(text, cons, prov, () -> true, table);
    }
    void check(String text, Boolc cons, Boolp prov, Boolp condition, Table table) {
        String infoText = text.substring(1) + ".info";
        var cell = table.check(text, cons).checked(prov.get()).update(a -> a.setDisabled(!condition.get())).left().padRight(15);
        if (Core.bundle.has(infoText)) {
            cell.tooltip(text + ".info", true);
        }
        cell.get().left();
        loop++;
        if (loop % 2 == 0) {
            table.row();
        }
    }
}
