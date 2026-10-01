package advdiff;

import advdiff.visual.AdvDiffStyles;
import arc.Core;
import arc.Events;
import arc.func.*;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.event.Touchable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.*;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Planets;
import mindustry.core.GameState;
import mindustry.ctype.ContentType;
import mindustry.editor.BannedContentDialog;
import mindustry.game.EventType;
import mindustry.game.Rules;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.type.Planet;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.Block;

import static advdiff.AdvancedDifficulties.rulesMap;
import static advdiff.RuleTeam.enemy;
import static arc.Core.graphics;
import static arc.Core.settings;
import static mindustry.Vars.*;
import static mindustry.graphics.Drawf.text;


public class AdvDiffDifficultyExtrasDialog extends BaseDialog {
    Planet planet;
    Table current;
    AdvDiffCampaignRules customRule;
    RuleTeam team;
    Rules rules;
    private Table main;
    private Prov<Rules> resetter;
    //private LoadoutDialog loadoutDialog;
    private BannedContentDialog<Block> bannedBlocks = new BannedContentDialog<>("@bannedblocks", ContentType.block, Block::canBeBuilt);
    private BannedContentDialog<UnitType> bannedUnits = new BannedContentDialog<>("@bannedunits", ContentType.unit, u -> !u.isHidden());
    public Seq<Table> categories;
    public Seq<String> categoryNames;
    public String currentName = "";
    public String ruleSearch = "";
    public float calcDiff;
    public String calcDiffDesc = "";
    Table wasCurrent2;
    Table globalTeamRules = new Table();

    public AdvDiffDifficultyExtrasDialog() {
        super("@advdiff-extras");

        Events.run(EventType.SaveLoadEvent.class, () -> {
            if(!Vars.state.isCampaign() && !net.active()) {
                //rulesMap.get(Planets.sun).applyEvents();
            }
        });
        Events.run(EventType.Trigger.newGame, () -> {
            if(settings.getBool("advdiff-customGameDifficultySelect") && !Vars.state.isCampaign() && !net.active()) {
                showCustomGame(Planets.sun);
                //Yesp, I'm using the suns campaign rules for custom game
            }
        });

        hidden(() -> {
            if (this.planet != null && this.planet != Planets.sun) {
                this.planet.saveRules();
                rulesMap.save(planet, customRule);
                rulesMap.get(planet).applyPlanet(planet);
                Vars.ui.campaignRules.show(planet);
            } else if(Vars.state.isGame() && !Vars.state.isCampaign() && !net.active() && this.planet == Planets.sun) {
                this.planet.saveRules();
                rulesMap.save(Planets.sun, customRule);
                rulesMap.get(Planets.sun).apply(Planets.sun, Vars.state.rules);
                //rulesMap.get(Planets.sun).applyEvents();
                Call.setRules(Vars.state.rules);
                Vars.state.set(GameState.State.playing);
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
        team = enemy;
        customRule = rulesMap.get(planet);
        rebuild();
        show();
    }
    public void showCustomGame(Planet planet) {
        Time.run(settings.getBool("skipcoreanimation") ? 1f : 165f, () -> {
            //Log.info("Custom game dialog opened");
            this.planet = planet;
            team = enemy;
            customRule = rulesMap.get(planet);
            rebuild();
            show();
            Vars.state.set(GameState.State.paused);
        });
    }

    public void rebuild() {
        buttons.clear();

        setFillParent(true);
        shown(this::refresh);
        if (planet != Planets.sun) {
            buttons.button("@back", Icon.left, this::hide).size(210, 64.0F).center().bottom();
        } else buttons.button("@done", Icon.ok, this::hide).size(210, 64.0F).center().bottom();
        addCloseListener();

        categories = new Seq<>();
        categoryNames = new Seq<>();

        buttons.button("@settings.reset", Icon.refresh, () -> {

            customRule.enemy.blockDamageMultiplier = 100f;
            customRule.enemy.blockHealthMultiplier = 100f;
            customRule.enemy.unitDamageMultiplier = 100f;
            customRule.enemy.unitHealthMultiplier = 100f;
            customRule.enemy.unitCostMultiplier = 100f;
            customRule.enemy.unitBuildSpeedMultiplier = 100f;
            customRule.enemy.oneShotOneKill = false;

            customRule.player.blockDamageMultiplier = 100f;
            customRule.player.blockHealthMultiplier = 100f;
            customRule.player.unitDamageMultiplier = 100f;
            customRule.player.unitHealthMultiplier = 100f;
            customRule.player.unitCostMultiplier = 100f;
            customRule.player.unitBuildSpeedMultiplier = 100f;
            customRule.player.oneShotOneKill = false;

            customRule.allowLaunchLoadout = settings.getBool(planet.name + "advdiff-" + "PALLDef", planet.allowLaunchLoadout);
            customRule.launchCapacityMultiplier = settings.getFloat(planet.name + "advdiff-" + "PLCMDef", planet.launchCapacityMultiplier) * 100;
            customRule.allowLaunchSchematics = settings.getBool(planet.name + "advdiff-" + "PALSDef", planet.allowLaunchSchematics);
            customRule.buildCostMultiplier = 100f;
            customRule.buildSpeedMultiplier = 100f;
            customRule.deconstructRefundMultiplier = 50f;

            customRule.enemySpawnMultiplier = 100f;
            customRule.waveTimeMultiplier = 100f;
            customRule.initialWaveSpacingMultiplier = 100f;
            customRule.captureWaveMultiplier = 100f;
            customRule.dropZoneRadiusMultiplier = 100f;

            customRule.wavesPerWave = 1;
            customRule.tierIncrease = 0;
            customRule.moddedUnitsAsExtraTiers = 0;
            customRule.forceRadiance = 0;

            customRule.advDiff = AdvDiffDifficulty.normal;

            customRule.enemyTypeRandomizer = false;
            customRule.enrageOnMaxUnits = false;

            customRule.allowEditRules = false;

            refresh();
        }).marginLeft(12f).size(210, 64.0F);
    }

    void refresh(){
        setup();
        setupMain();
        requestKeyboard();
        requestScroll();
    }

    void setup(){
        cont.clear();
        cont.top().center().defaults().pad(10).row();

        cont.table(t -> {
            t.add("@calculated-global-difficulty").tooltip("@calculated-global-difficulty.info").center();
            if (mobile) t.button(Icon.infoSmall, () -> ui.showInfo("@calculated-global-difficulty.info")).center().size(32f);
        }).fill(false).expand(false, false);
        cont.row();
        cont.label(() -> {
            float calcDiffEnemy = (float) (
                    1 * Math.sqrt(customRule.enemy.blockDamageMultiplier/100) * Math.sqrt(customRule.enemy.blockHealthMultiplier/100)
                            * Math.sqrt(customRule.enemy.unitDamageMultiplier/100) * Math.sqrt(customRule.enemy.unitHealthMultiplier/100)
                            * ((1/(customRule.enemy.unitCostMultiplier/100)/4) + (customRule.enemy.unitBuildSpeedMultiplier/100/4)
                            + (customRule.enemySpawnMultiplier/100/2))
            );
            float calcDiffPlayer = (float) (
                    1 / Math.sqrt(customRule.player.blockDamageMultiplier/100) / Math.sqrt(customRule.player.blockHealthMultiplier/100)
                            / Math.sqrt(customRule.player.unitDamageMultiplier/100) / Math.sqrt(customRule.player.unitHealthMultiplier/100)
                            / (1/(customRule.player.unitCostMultiplier/100)/2 + (customRule.player.unitBuildSpeedMultiplier/100/2))
                            / (1/(customRule.buildCostMultiplier/100)) / (customRule.buildSpeedMultiplier/100)
                            / (customRule.deconstructRefundMultiplier <= 10f ? 0.2f : Math.sqrt(customRule.deconstructRefundMultiplier/50))
            );
            float calcDiffOther = (float) (
                    1 / Math.pow(customRule.waveTimeMultiplier/100, 2) * Math.pow(customRule.wavesPerWave, 2)
                            * Math.pow(5 ,customRule.tierIncrease) * Math.pow(5 ,customRule.forceRadiance)

                            * (customRule.allowEditRules ? 0 : 1)
            );
            calcDiff = calcDiffEnemy * calcDiffPlayer * calcDiffOther;
            calcDiffDesc = "difficulty.normal";
            if (calcDiff < 0) calcDiffDesc = "difficulty.negative";
            if (calcDiff == 0) calcDiffDesc = "difficulty.cheats";
            if (calcDiff > 0) calcDiffDesc = "difficulty.walk-in-the-park";
            if (calcDiff >= 0.01) calcDiffDesc = "difficulty.casual";
            if (calcDiff >= 0.1) calcDiffDesc = "difficulty.easy";
            if (calcDiff >= 0.5) calcDiffDesc = "difficulty.slightly-easier";
            if (calcDiff >= 0.75) calcDiffDesc = "difficulty.normal";
            if (calcDiff >= 2) calcDiffDesc = "difficulty.hard";
            if (calcDiff >= 4) calcDiffDesc = "difficulty.extreme";
            if (calcDiff >= 7) calcDiffDesc = "difficulty.eradication";
            if (calcDiff >= 14) calcDiffDesc = "difficulty.annihilation";
            if (calcDiff >= 22) calcDiffDesc = "difficulty.extermination";
            if (calcDiff >= 36) calcDiffDesc = "difficulty.catastrophic";
            if (calcDiff >= 64) calcDiffDesc = "difficulty.horrific";
            if (calcDiff >= 120) calcDiffDesc = "difficulty.unreal";
            if (calcDiff >= 240) calcDiffDesc = "difficulty.nil";
            if (calcDiff >= 500) calcDiffDesc = "difficulty.mindustry-must-die";
            if (calcDiff >= 50000000) calcDiffDesc = "difficulty.sisyphus";
            if (calcDiff >= 100000000000000000000f) calcDiffDesc = "difficulty.blablabla";
            calcDiffDesc = Core.bundle.get(calcDiffDesc);
            return calcDiff > 1000000 || calcDiff < 1 ? "x" + calcDiff + " - " + calcDiffDesc : "x" + (float)((int)(calcDiff *100))/100 + " - " + calcDiffDesc;
        }).tooltip("@calculated-global-difficulty.info").center().pad(10).row();

        //Casual = 0.0625
        //Easy = 0.3334
        //Normal = 1
        //Hard = 2.92
        //Eradication = 8.33
        //Unreasonable = 27

        Cell<ScrollPane> paneCell = cont.pane(m -> main = m);

        paneCell.scrollX(main.getPrefWidth() + 40f > graphics.getWidth());
    }

    void setupMain(){

        categories.clear();
        main.clear();
        main.left().defaults().fillX().left();
        main.row();
        main.marginRight(25f);

        if (planet == Planets.sun) main.table(t -> {
            t.margin(10f);
            var group = new ButtonGroup<>();

            String waveTeam = Vars.state.rules.waveTeam.name;
            if (Core.atlas.find("advdiff-" + waveTeam + "-normal") == Core.atlas.find("error")) waveTeam = Team.crux.name;

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
                    if (mobile || Core.graphics.getAspect() < 16f/9f) iconScale = 0.75f; else iconScale = 1f;
                    t.button(icon, style, () -> {
                                customRule.set(diff);
                                //if (mobile) ui.showInfo(diff.info());
                                setupMain();
                            }).padLeft(padL).padRight(padR).padTop(padT).group(group).checked(b -> customRule.advDiff == diff).tooltip(diff.info(), true)
                            .with(i -> i.getImage().touchable = Touchable.disabled)
                            .with(i -> i.getImage().scaleX = iconScale).with(i -> i.getImage().scaleY = iconScale);
                }
                if (Core.graphics.isPortrait() && diff.ordinal() % 3 == 2) {
                    t.row();
                }
            }
        }).center().fill(false).expand(false, false).row();

        if (planet != Planets.sun) {
            category("launching");
            current.table(t -> {
                check(t, "@advdiff-allowLaunchLoadout", b -> customRule.allowLaunchLoadout = b, () -> customRule.allowLaunchLoadout);
                slider(t, "@advdiff-launchCapacityMultiplier", f -> customRule.launchCapacityMultiplier = f, () -> customRule.launchCapacityMultiplier, 0F, 100F, 0f, 100f, 5f, "%");
                check(t, "@advdiff-allowLaunchSchematics", b -> customRule.allowLaunchSchematics = b, () -> customRule.allowLaunchSchematics);
            }).fill(false).expand(false, false).row();
        }

        category("teams");
        current.table(Tex.button, t -> {
            t.margin(10f);
            var group = new ButtonGroup<>();
            var style = Styles.emptyTogglei;

            rules = new Rules();
            planet.ruleSetter.get(rules);
            if (planet == Planets.sun) rules = Vars.state.rules;
            Team enemy = rules.waveTeam;
            Team player = rules.defaultTeam;

            t.defaults().size(80f, 80f);
            //for(var rt : RuleTeam.all){
            if (Core.atlas.find("team-" + enemy.name) != Core.atlas.find("error")) {
                t.button(Core.atlas.drawable("team-" + enemy.name), style, () -> {
                    team = RuleTeam.enemy;
                    refresh();
                }).tooltip(enemy.coloredName()).group(group).checked(b -> team == RuleTeam.enemy);
            } else {
                t.button(Core.atlas.drawable("advdiff-team-enemy"), style, () -> {
                    team = RuleTeam.enemy;
                    refresh();
                }).tooltip(enemy.coloredName()).group(group).checked(b -> team == RuleTeam.enemy).with(i -> i.getStyle().imageCheckedColor = enemy.color).with(i -> i.getStyle().imageDownColor = enemy.color).with(i -> i.getStyle().imageUpColor = enemy.color.cpy().value(0.5f));
            }
            if (Core.atlas.find("team-" + player.name) != Core.atlas.find("error")) {
                t.button(Core.atlas.drawable("team-" + player.name), style, () -> {
                    team = RuleTeam.player;
                    refresh();
                }).tooltip(player.coloredName()).group(group).checked(b -> team == RuleTeam.player);
            } else {
                t.button(Core.atlas.drawable("advdiff-team-player"), style, () -> {
                    team = RuleTeam.player;
                    refresh();
                }).tooltip(player.coloredName()).group(group).checked(b -> team == RuleTeam.player).with(i -> i.getStyle().imageCheckedColor = player.color).with(i -> i.getStyle().imageDownColor = player.color).with(i -> i.getStyle().imageUpColor = player.color.cpy().value(0.5f));
            }
            //}
        }).growX().padLeft(10f).fill(false).row();

        /*
        current.table(t -> {
            wasCurrent2 = this.current;
            globalTeamRules = new Table();
            this.current = t;
            globalTeamSlider(t, "@advdiff-globalTeamMultiplier", f -> {
                customRule.team(team).blockHealthMultiplier = f;
                customRule.team(team).blockDamageMultiplier = f;
                customRule.team(team).unitHealthMultiplier = f;
                customRule.team(team).unitDamageMultiplier = f;
                customRule.team(team).unitCostMultiplier = 1/(f/100)*100;
                customRule.team(team).unitBuildSpeedMultiplier = f;
            }, () -> ((customRule.team(team).blockHealthMultiplier) + (customRule.team(team).blockDamageMultiplier)
                            + (customRule.team(team).unitHealthMultiplier) + (customRule.team(team).unitDamageMultiplier)
                    + (1/(customRule.team(team).unitCostMultiplier/100)*100) + (customRule.team(team).unitBuildSpeedMultiplier)) / 6
                    , 1F, 10000F, 10f, 500f, 10f, "%");
            current = wasCurrent2;
        }).fill(false).expand(false, false).row();
         */

        current.table(t -> {
            slider(t, "@rules.blockhealthmultiplier", f -> customRule.team(team).blockHealthMultiplier = f, () -> customRule.team(team).blockHealthMultiplier, 1F, 10000F, 10f, 500f, 25f, "%");
            slider(t, "@rules.blockdamagemultiplier", f -> customRule.team(team).blockDamageMultiplier = f, () -> customRule.team(team).blockDamageMultiplier, 1F, 10000F, 10f, 500f, 25f, "%");
            slider(t, "@rules.unithealthmultiplier", f -> customRule.team(team).unitHealthMultiplier = f, () -> customRule.team(team).unitHealthMultiplier, 1F, 10000F, 10f, 500f, 25f, "%");
            slider(t, "@rules.unitdamagemultiplier", f -> customRule.team(team).unitDamageMultiplier = f, () -> customRule.team(team).unitDamageMultiplier, 1F, 10000F, 10f, 500f, 25f, "%");
            slider(t, "@rules.unitcostmultiplier", f -> customRule.team(team).unitCostMultiplier = f, () -> customRule.team(team).unitCostMultiplier, 1F, 10000F, 10f, 500f, 25f, "%");
            slider(t, "@rules.unitbuildspeedmultiplier", f -> customRule.team(team).unitBuildSpeedMultiplier = f, () -> customRule.team(team).unitBuildSpeedMultiplier, 1F, 10000F, 10f, 500f, 25f, "%");
            check(t, "@advdiff-oneShotOneKill", b -> customRule.team(team).oneShotOneKill = b, () -> customRule.team(team).oneShotOneKill);
        }).fill(false).expand(false, false).row();
        if (team == RuleTeam.player) {
            current.table(t -> {
                slider(t, "@rules.buildcostmultiplier", f -> customRule.buildCostMultiplier = f, () -> customRule.buildCostMultiplier, 1F, 10000F, 10f, 200f, 10f, "%");
                slider(t, "@rules.buildspeedmultiplier", f -> customRule.buildSpeedMultiplier = f, () -> customRule.buildSpeedMultiplier, 1F, 10000F, 10f, 200f, 10f, "%");
                slider(t, "@rules.deconstructrefundmultiplier", f -> customRule.deconstructRefundMultiplier = f, () -> customRule.deconstructRefundMultiplier, 0F, 100F, 0f, 100f, 10f, "%");
            }).fill(false).expand(false, false);
        }

        category("waves");
        current.table(t -> {
            slider(t, "@advdiff-enemySpawnMultiplier", f -> customRule.enemySpawnMultiplier = f, () -> customRule.enemySpawnMultiplier, 0F, 10000F, 25f, 500f, 25f, "%");
            slider(t, "@advdiff-waveTimeMultiplier", f -> customRule.waveTimeMultiplier = f, () -> customRule.waveTimeMultiplier, 1F, 10000F, 25f, 500f, 25f, "%");
            slider(t, "@advdiff-initialWaveSpacingMultiplier", f -> customRule.initialWaveSpacingMultiplier = f, () -> customRule.initialWaveSpacingMultiplier, 10f, 500f, 25f, "%");
            slider(t, "@advdiff-dropZoneRadiusMultiplier", f -> customRule.dropZoneRadiusMultiplier = f, () -> customRule.dropZoneRadiusMultiplier, 0f, 500f, 25f, "%");
            slider(t, "@advdiff-captureWaveMultiplier", f -> customRule.captureWaveMultiplier = f, () -> customRule.captureWaveMultiplier, 0f, 500f, 25f, "%");
            t.row();
            check(t, "@advdiff-enemyTypeRandomizer", b -> customRule.enemyTypeRandomizer = b, () -> customRule.enemyTypeRandomizer);
            check(t, "@advdiff-shieldsToRadiance", b -> customRule.shieldsToRadiance = b, () -> customRule.shieldsToRadiance);
            sliderInt(t, "@advdiff-wavesPerWave", f -> customRule.wavesPerWave = (int) f, () -> customRule.wavesPerWave, 1, 100, 1, 10, "@waves");
            sliderInt(t, "@advdiff-forceRadiance", f -> customRule.forceRadiance = (int) f, () -> customRule.forceRadiance, -7, 7, -4, 4, "@tiers");
            sliderInt(t, "@advdiff-tierIncrease", f -> customRule.tierIncrease = (int) f, () -> customRule.tierIncrease, -8, 8, -3, 3, "@tiers");
        }).fill(false).expand(false, false).row();

        current.table(t -> {
            boolean[] shown = new boolean[]{false};
            Table wasCurrent = this.current;
            Table moddedAsExtraTiers = new Table();
            t.button("@advdiff-moddedAsExtraTiers", Icon.downOpen, Styles.togglet, () -> {
                shown[0] = !shown[0];
            }).marginLeft(14.0F).width(360.0F).height(64.0F).update((t2) -> {
                ((Image)t2.getChildren().get(1)).setDrawable(shown[0] ? Icon.upOpen : Icon.downOpen);
                t2.setChecked(shown[0]);
            }).left().padBottom(2.0F).row();
            t.collapser((c) -> {
                c.left().defaults().fillX().left().pad(5.0F);
                this.current = c;

                setCheck(c, "@advdiff-exogenesisoldAsExtraTiers", 1);
                //setCheck(c, "@advdiff-exogenesisAsExtraTiers", 2);
                if (planet != Planets.erekir) setCheck(c, "@advdiff-newHorizonAsExtraTiers", 3);

                if(!current.hasChildren()){
                    moddedAsExtraTiers.clear();
                }else {
                    wasCurrent.add(moddedAsExtraTiers).row();
                }
                current = wasCurrent;
            }, () -> shown[0]).left().growX().row();
        }).fill(false).expand(false, false);

        category("cheats");
        current.table(t -> {
            check(t, "@rules.allowedit", b -> customRule.allowEditRules = b, () -> customRule.allowEditRules);
        }).fill(false).expand(false, false);

        /*
        category("waves");
        check("@rules.waves", b -> rules.waves = b, () -> rules.waves);
        check("@rules.wavesending", b -> rules.waveSending = b, () -> rules.waveSending, () -> rules.waves);
        check("@rules.wavetimer", b -> rules.waveTimer = b, () -> rules.waveTimer, () -> rules.waves);
        check("@rules.waitForWaveToEnd", b -> rules.waitEnemies = b, () -> rules.waitEnemies, () -> rules.waves && rules.waveTimer);
        check("@rules.randomwaveai", b -> rules.randomWaveAI = b, () -> rules.randomWaveAI, () -> rules.waves);
        check("@rules.wavespawnatcores", b -> rules.wavesSpawnAtCores = b, () -> rules.wavesSpawnAtCores, () -> rules.waves);
        check("@rules.airUseSpawns", b -> rules.airUseSpawns = b, () -> rules.airUseSpawns, () -> rules.waves);
        numberi("@rules.wavelimit", f -> rules.winWave = f, () -> rules.winWave, () -> rules.waves, 0, Integer.MAX_VALUE);
        number("@rules.wavespacing", false, f -> rules.waveSpacing = f * 60f, () -> rules.waveSpacing / 60f, () -> rules.waves && rules.waveTimer, 1, Float.MAX_VALUE);
        number("@rules.initialwavespacing", false, f -> rules.initialWaveSpacing = f * 60f, () -> rules.initialWaveSpacing / 60f, () -> rules.waves && rules.waveTimer, 0, Float.MAX_VALUE);
        number("@rules.dropzoneradius", false, f -> rules.dropZoneRadius = f * tilesize, () -> rules.dropZoneRadius / tilesize, () -> rules.waves);

        category("resourcesbuilding");
        check("@rules.alloweditworldprocessors", b -> rules.allowEditWorldProcessors = b, () -> rules.allowEditWorldProcessors);
        check("@rules.infiniteresources", b -> rules.infiniteResources = b, () -> rules.infiniteResources);
        check("@rules.onlydepositcore", b -> rules.onlyDepositCore = b, () -> rules.onlyDepositCore);
        check("@rules.coreunloaders", b -> rules.allowCoreUnloaders = b, () -> rules.allowCoreUnloaders);
        check("@rules.derelictrepair", b -> rules.derelictRepair = b, () -> rules.derelictRepair);
        check("@rules.reactorexplosions", b -> rules.reactorExplosions = b, () -> rules.reactorExplosions);
        check("@rules.schematic", b -> rules.schematicsAllowed = b, () -> rules.schematicsAllowed);
        check("@rules.coreincinerates", b -> rules.coreIncinerates = b, () -> rules.coreIncinerates);
        check("@rules.cleanupdeadteams", b -> rules.cleanupDeadTeams = b, () -> rules.cleanupDeadTeams, () -> rules.pvp);
        check("@rules.disableworldprocessors", b -> rules.disableWorldProcessors = b, () -> rules.disableWorldProcessors);
        number("@rules.buildcostmultiplier", false, f -> rules.buildCostMultiplier = f, () -> rules.buildCostMultiplier, () -> !rules.infiniteResources);
        number("@rules.buildspeedmultiplier", f -> rules.buildSpeedMultiplier = f, () -> rules.buildSpeedMultiplier, 0.001f, 50f);
        number("@rules.deconstructrefundmultiplier", false, f -> rules.deconstructRefundMultiplier = f, () -> rules.deconstructRefundMultiplier, () -> !rules.infiniteResources, 0f, 1f);
        number("@rules.blockhealthmultiplier", f -> rules.blockHealthMultiplier = f, () -> rules.blockHealthMultiplier);
        number("@rules.blockdamagemultiplier", f -> rules.blockDamageMultiplier = f, () -> rules.blockDamageMultiplier);

        if(Core.bundle.get("configure").toLowerCase().contains(ruleSearch)){
            current.button("@configure",
                    () -> loadoutDialog.show(999999, rules.loadout,
                            i -> true,
                            () -> rules.loadout.clear().add(new ItemStack(Items.copper, 100)),
                            () -> {}, () -> {}
                    )).left().width(300f).row();
        }

        if(Core.bundle.get("bannedblocks").toLowerCase().contains(ruleSearch)){
            current.button("@bannedblocks", () -> bannedBlocks.show(rules.bannedBlocks)).left().width(300f).row();
        }
        check("@rules.hidebannedblocks", b -> rules.hideBannedBlocks = b, () -> rules.hideBannedBlocks);
        check("@bannedblocks.whitelist", b -> rules.blockWhitelist = b, () -> rules.blockWhitelist);

        category("unit");
        check("@rules.unitcapvariable", b -> rules.unitCapVariable = b, () -> rules.unitCapVariable);
        check("@rules.unitpayloadsexplode", b -> rules.unitPayloadsExplode = b, () -> rules.unitPayloadsExplode);
        numberi("@rules.unitcap", f -> rules.unitCap = f, () -> rules.unitCap, -999, 999);

        number("@rules.unitfactoryactivation", f -> rules.unitFactoryActivationDelay = f * 60f, () -> rules.unitFactoryActivationDelay / 60f);
        number("@rules.unitdamagemultiplier", f -> rules.unitDamageMultiplier = f, () -> rules.unitDamageMultiplier);
        number("@rules.unitcrashdamagemultiplier", f -> rules.unitCrashDamageMultiplier = f, () -> rules.unitCrashDamageMultiplier);
        number("@rules.unitminespeedmultiplier", f -> rules.unitMineSpeedMultiplier = f, () -> rules.unitMineSpeedMultiplier);
        number("@rules.unitbuildspeedmultiplier", f -> rules.unitBuildSpeedMultiplier = f, () -> rules.unitBuildSpeedMultiplier, 0f, 50f);
        number("@rules.unitcostmultiplier", f -> rules.unitCostMultiplier = f, () -> rules.unitCostMultiplier);
        check("@rules.logicunitcontrol", b -> rules.logicUnitControl = b, () -> rules.logicUnitControl);
        check("@rules.logicunitbuild", b -> rules.logicUnitBuild = b, () -> rules.logicUnitBuild, () -> rules.logicUnitControl);
        check("@rules.logicunitdeconstruct", b -> rules.logicUnitDeconstruct = b, () -> rules.logicUnitDeconstruct, () -> rules.logicUnitControl);

        if(Core.bundle.get("bannedunits").toLowerCase().contains(ruleSearch)){
            current.button("@bannedunits", () -> bannedUnits.show(rules.bannedUnits)).left().width(300f).row();
        }
        check("@bannedunits.whitelist", b -> rules.unitWhitelist = b, () -> rules.unitWhitelist);

        category("enemy");
        check("@rules.attack", b -> rules.attackMode = b, () -> rules.attackMode);
        check("@rules.corecapture", b -> rules.coreCapture = b, () -> rules.coreCapture);
        check("@rules.placerangecheck", b -> rules.placeRangeCheck = b, () -> rules.placeRangeCheck);
        check("@rules.polygoncoreprotection", b -> rules.polygonCoreProtection = b, () -> rules.polygonCoreProtection);
        number("@rules.enemycorebuildradius", f -> rules.enemyCoreBuildRadius = f * tilesize, () -> Math.min(rules.enemyCoreBuildRadius / tilesize, 200), () -> !rules.polygonCoreProtection);

        category("environment");
        check("@rules.pauseDisabled", b -> rules.pauseDisabled = b, () -> rules.pauseDisabled);
        check("@rules.explosions", b -> rules.damageExplosions = b, () -> rules.damageExplosions);
        check("@rules.fire", b -> rules.fire = b, () -> rules.fire);
        check("@rules.fog", b -> rules.fog = b, () -> rules.fog);
        check("@rules.lighting", b -> rules.lighting = b, () -> rules.lighting);

        check("@rules.limitarea", b -> rules.limitMapArea = b, () -> rules.limitMapArea, () -> !state.isGame());
        numberi("x", x -> rules.limitX = x, () -> rules.limitX, () -> rules.limitMapArea && !state.isGame(), 0, 10000);
        numberi("y", y -> rules.limitY = y, () -> rules.limitY, () -> rules.limitMapArea && !state.isGame(), 0, 10000);
        numberi("w", w -> rules.limitWidth = w, () -> rules.limitWidth, () -> rules.limitMapArea && !state.isGame(), 0, 10000);
        numberi("h", h -> rules.limitHeight = h, () -> rules.limitHeight, () -> rules.limitMapArea && !state.isGame(), 0, 10000);

        number("@rules.solarmultiplier", f -> rules.solarMultiplier = f, () -> rules.solarMultiplier);

        if(Core.bundle.get("rules.ambientlight").toLowerCase().contains(ruleSearch)){
            current.button(b -> {
                b.left();
                b.table(Tex.pane, in -> {
                    in.stack(new Image(Tex.alphaBg), new Image(Tex.whiteui){{
                        update(() -> setColor(rules.ambientLight));
                    }}).grow();
                }).margin(4).size(50f).padRight(10);
                b.add("@rules.ambientlight");
            }, () -> ui.picker.show(rules.ambientLight, rules.ambientLight::set)).left().width(250f).row();
        }

        Boolp allowMusic = () -> !rules.disableMusic;
        Func<String, Seq<MusicContainer>> parser = str -> {
            try{
                return Seq.map(new JsonReader().parse("[" + str + "]").asStringArray(), MusicContainer::new);
            }catch(Throwable e){
                return null;
            }
        };

        check("@rules.alwaysplaymusic", b -> rules.alwaysPlayMusic = b, () -> rules.alwaysPlayMusic, allowMusic);
        check("@rules.nomusic", b -> rules.disableMusic = b, () -> rules.disableMusic);

        category("planet");
        if(Core.bundle.get("rules.title.planet").toLowerCase().contains(ruleSearch)){
            current.table(Tex.button, t -> {
                t.margin(10f);
                var group = new ButtonGroup<>();
                var style = Styles.flatTogglet;

                t.defaults().size(140f, 50f);

                for(Planet planet : content.planets().select(p -> p.accessible && p.visible && p.isLandable())){
                    t.button(planet.localizedName, style, () -> {
                        planet.applyRules(rules, true);
                    }).group(group).checked(b -> rules.planet == planet);

                    if(t.getChildren().size % 3 == 0){
                        t.row();
                    }
                }

                t.button("@rules.anyenv", style, () -> {
                    rules.attributes.clear();
                    rules.env = Vars.defaultEnv;
                    rules.planet = Planets.sun;
                }).group(group).checked(b -> rules.planet == Planets.sun);
            }).left().fill(false).expand(false, false).row();
        }

        category("teams");
        //not sure where else to put this
        if(showRuleEditRule){
            check("@rules.allowedit", b -> rules.allowEditRules = b, () -> rules.allowEditRules);
        }
        team("@rules.playerteam", t -> rules.defaultTeam = t, () -> rules.defaultTeam);
        team("@rules.enemyteam", t -> rules.waveTeam = t, () -> rules.waveTeam);

        for(Team team : Team.baseTeams){
            boolean[] shown = {false};
            Table wasCurrent = current;

            Table teamRules = new Table(); // just button and collapser in one table
            teamRules.button(team.coloredName(), Icon.downOpen, Styles.togglet, () -> {
                shown[0] = !shown[0];
            }).marginLeft(14f).width(260f).height(55f).update(t -> {
                ((Image)t.getChildren().get(1)).setDrawable(shown[0] ? Icon.upOpen : Icon.downOpen);
                t.setChecked(shown[0]);
            }).left().padBottom(2f).row();

            teamRules.collapser(c -> {
                c.left().defaults().fillX().left().pad(5);
                current = c;
                Rules.TeamRule teams = rules.teams.get(team);

                number("@rules.blockhealthmultiplier", f -> teams.blockHealthMultiplier = f, () -> teams.blockHealthMultiplier);
                number("@rules.blockdamagemultiplier", f -> teams.blockDamageMultiplier = f, () -> teams.blockDamageMultiplier);

                check("@rules.rtsai", b -> teams.rtsAi = b, () -> teams.rtsAi, () -> team != rules.defaultTeam);
                numberi("@rules.rtsminsquadsize", f -> teams.rtsMinSquad = f, () -> teams.rtsMinSquad, () -> teams.rtsAi, 0, 100);
                numberi("@rules.rtsmaxsquadsize", f -> teams.rtsMaxSquad = f, () -> teams.rtsMaxSquad, () -> teams.rtsAi, 1, 1000);
                number("@rules.rtsminattackweight", f -> teams.rtsMinWeight = f, () -> teams.rtsMinWeight, () -> teams.rtsAi);

                //disallow on Erekir (this is broken for mods I'm sure, but whatever)
                check("@rules.buildai", b -> teams.buildAi = b, () -> teams.buildAi, () -> team != rules.defaultTeam && rules.env != Planets.erekir.defaultEnv && !rules.pvp);
                number("@rules.buildaitier", false, f -> teams.buildAiTier = f, () -> teams.buildAiTier, () -> teams.buildAi && rules.env != Planets.erekir.defaultEnv && !rules.pvp, 0, 1);

                check("@rules.protectcores", b -> teams.protectCores = b, () -> teams.protectCores);
                number("@rules.extracorebuildradius", f -> teams.extraCoreBuildRadius = f * tilesize, () -> Math.min(teams.extraCoreBuildRadius / tilesize, 200), () -> !rules.polygonCoreProtection && teams.protectCores);
                check("@rules.checkplacement", b -> teams.checkPlacement = b, () -> teams.checkPlacement);

                check("@rules.infiniteresources", b -> teams.infiniteResources = b, () -> teams.infiniteResources);
                check("@rules.fillitems", b -> teams.fillItems = b, () -> teams.fillItems);
                number("@rules.buildspeedmultiplier", f -> teams.buildSpeedMultiplier = f, () -> teams.buildSpeedMultiplier, 0.001f, 50f);

                number("@rules.unitfactoryactivation", f -> teams.unitFactoryActivationDelay = f * 60f, () -> teams.unitFactoryActivationDelay / 60f);
                number("@rules.unitdamagemultiplier", f -> teams.unitDamageMultiplier = f, () -> teams.unitDamageMultiplier);
                number("@rules.unitcrashdamagemultiplier", f -> teams.unitCrashDamageMultiplier = f, () -> teams.unitCrashDamageMultiplier);
                number("@rules.unitminespeedmultiplier", f -> teams.unitMineSpeedMultiplier = f, () -> teams.unitMineSpeedMultiplier);
                number("@rules.unitbuildspeedmultiplier", f -> teams.unitBuildSpeedMultiplier = f, () -> teams.unitBuildSpeedMultiplier, 0.001f, 50f);
                number("@rules.unitcostmultiplier", f -> teams.unitCostMultiplier = f, () -> teams.unitCostMultiplier);
                number("@rules.unithealthmultiplier", f -> teams.unitHealthMultiplier = f, () -> teams.unitHealthMultiplier);

                if(!current.hasChildren()){
                    teamRules.clear();
                }else{
                    wasCurrent.add(teamRules).row();
                }

                current = wasCurrent;
            }, () -> shown[0]).left().growX().row();

        }
         */
        //additionalSetup.each(Runnable::run);

        for(var i = 0; i < categories.size; i++){
            addToMain(categories.get(i), Core.bundle.get("rules.title." + categoryNames.get(i)));
        }
    }

    public void category(String name){
        current = new Table();
        current.left().defaults().fillX().expandX().left().pad(5);
        currentName = name;
        categories.add(current);
        categoryNames.add(currentName);
    }

    void addToMain(Table category, String title){
        if(category.hasChildren()){
            main.add(title).color(Pal.accent).padTop(20).padRight(100f).padBottom(-3).fillX().left().pad(5).row();
            main.image().color(Pal.accent).height(3f).padRight(100f).padBottom(20).fillX().left().pad(5).row();
            main.add(category).row();
        }
    }

    public void slider(Table t, String text, Floatc cons, Floatp prov, float sliderMin, float sliderMax, float step, String tail){
        slider(t, text, false, cons, prov, () -> true, 0, Float.MAX_VALUE, sliderMin, sliderMax, step, tail);
    }

    public void slider(Table t, String text, Floatc cons, Floatp prov, float min, float max, float sliderMin, float sliderMax, float step, String tail){
        slider(t, text, false, cons, prov, () -> true, min, max, sliderMin, sliderMax, step, tail);
    }

    public void sliderInt(Table t, String text, Floatc cons, Intp prov, int min, int max, float sliderMin, float sliderMax, String tail){
        sliderInt(t, text, cons, prov, () -> true, min, max, sliderMin, sliderMax, tail);
    }

    public void sliderInt(Table t, String text, Floatc cons, Intp prov, Boolp condition, int min, int max, float sliderMin, float sliderMax, String tail){
        if(!Core.bundle.get(text.substring(1)).toLowerCase().contains(ruleSearch)) return;
        t.left();
        var cell = t.add(text).left().padRight(5).tooltip(text + ".info")
                .update(a -> a.setColor(condition.get() ? Color.white : Color.gray));
        Slider slider = new Slider(sliderMin, sliderMax, 1, false);
        Slider slider2 = new Slider(min, max, 1, false);
        slider.setValue(prov.get());
        TextField field = new TextField((int)prov.get() + "");
        field.changed(() -> {
            try {
                int parsedValue = Strings.parseInt(field.getText());
                parsedValue = Mathf.clamp(parsedValue, min, max);
                slider.setValue(parsedValue);
                slider2.setValue(parsedValue);
            } catch (NumberFormatException ignored) {}
        });
        slider.moved(cons);
        slider2.moved(cons);
        slider.changed(() -> {
            customRule.advDiff = AdvDiffDifficulty.normal;
            field.setText(String.valueOf((int)slider.getValue()));
        });

        t.add(slider).left().padRight(5).tooltip(text + ".info");
        t.add(field).left().padRight(5).tooltip(text + ".info");
        t.add(tail).left().tooltip(text + ".info")
                .update(a -> a.setColor(condition.get() ? Color.white : Color.gray));
        ruleInfo(cell, text);
        t.row();
    }

    public void slider(Table t, String text, boolean integer, Floatc cons, Floatp prov, Boolp condition, float min, float max, float sliderMin, float sliderMax, float step, String tail){
        if(!Core.bundle.get(text.substring(1)).toLowerCase().contains(ruleSearch)) return;
        t.left();
        var cell = t.add(text).left().padRight(5)
                .update(a -> a.setColor(condition.get() ? Color.white : Color.gray));
        Slider slider = new Slider(sliderMin, sliderMax, step, false);
        Slider slider2 = new Slider(min, max, 0.01f, false);
        slider.setValue(prov.get());
        TextField field = new TextField(prov.get() + "");
        field.changed(() -> {
            try {
                float parsedValue = Strings.parseFloat(field.getText());
                parsedValue = Mathf.clamp(parsedValue, min, max);
                slider.setValue(parsedValue);
                slider2.setValue(parsedValue);
            } catch (NumberFormatException ignored) {}
        });
        slider.moved(cons);
        slider2.moved(cons);
        slider.changed(() -> {
            customRule.advDiff = AdvDiffDifficulty.normal;
            field.setText(String.format("%.1f", slider.getValue()));
        });
        if(Core.bundle.has(text.substring(1) + ".info")){
            t.add(slider).left().padRight(5).tooltip(text + ".info");
            t.add(field).left().padRight(5).tooltip(text + ".info");
            t.add(tail).left().tooltip(text + ".info")
                    .update(a -> a.setColor(condition.get() ? Color.white : Color.gray));
        } else {
            t.add(slider).left().padRight(5);
            t.add(field).left().padRight(5);
            t.add(tail).left()
                    .update(a -> a.setColor(condition.get() ? Color.white : Color.gray));
        }
        ruleInfo(cell, text);
        t.row();
    }

    public void globalTeamSlider(Table t, String text, Floatc cons, Floatp prov, float min, float max, float sliderMin, float sliderMax, float step, String tail){
        globalTeamSlider(t, text, cons, prov, () -> true, min, max, sliderMin, sliderMax, step, tail);
    }
    public void globalTeamSlider(Table t, String text, Floatc cons, Floatp prov, Boolp condition, float min, float max, float sliderMin, float sliderMax, float step, String tail){
        //if(!Core.bundle.get(text.substring(1)).toLowerCase().contains(ruleSearch)) return;
        t.left();
        t.add(text).left().padRight(5).tooltip(text + ".info")
                .update(a -> a.setColor(condition.get() ? Color.white : Color.gray));
        Slider slider = new Slider(sliderMin, sliderMax, step, false);
        Slider slider2 = new Slider(min, max, 0.01f, false);
        slider.setValue(prov.get());
        TextField field = new TextField(prov.get() + "");
        field.changed(() -> {
            try {
                float parsedValue = Strings.parseFloat(field.getText());
                parsedValue = Mathf.clamp(parsedValue, min, max);
                slider.setValue(parsedValue);
                slider2.setValue(parsedValue);
                globalTeamRules.clear();
                wasCurrent2.add(globalTeamRules).row();
            } catch (NumberFormatException ignored) {}
        });
        slider.moved(cons);
        slider2.moved(cons);
        slider.changed(() -> {
            customRule.advDiff = AdvDiffDifficulty.normal;
            field.setText(String.format("%.1f", slider.getValue()));
            globalTeamRules.clear();
            wasCurrent2.add(globalTeamRules).row();
        });
        t.add(slider).left().padRight(5).tooltip(text + ".info");
        t.add(field).left().padRight(5).tooltip(text + ".info");
        t.add(tail).left().tooltip(text + ".info")
                .update(a -> a.setColor(condition.get() ? Color.white : Color.gray));
        t.row();
    }

    public void ruleInfo(Cell<?> cell, String text){
        if(Core.bundle.has(text.substring(1) + ".info")){
            //disabled in portrait - broken and goes offscreen
            //Idk why he said that, it kinda doesn't
            if(mobile){
                Table table = new Table();
                table.add(cell.get()).left().expandX().fillX();
                cell.clearElement();
                table.button(Icon.infoSmall, () -> ui.showInfo(text + ".info")).size(32f).right();
                cell.setElement(table).left().expandX().fillX();
            }else{
                cell.tooltip(text + ".info");
            }
        }
    }

    void check(Table t, String text, Boolc cons, Boolp prov) {
        check(t, text, cons, prov, () -> true);
    }

    void check(Table t, String text, Boolc cons, Boolp prov, Boolp condition) {
        var cell = t.check(text, cons).checked(prov.get()).update(a -> a.setDisabled(!condition.get())).left();
        cell.get().left();
        ruleInfo(cell, text);
        t.row();
    }

    void setCheck(Table t, String text, int num) {
        var cell = t.check(text, c -> {
            if (customRule.moddedUnitsAsExtraTiers != num) {
                customRule.moddedUnitsAsExtraTiers = num;
            } else customRule.moddedUnitsAsExtraTiers = 0;
        }).checked(b -> customRule.moddedUnitsAsExtraTiers == num).left();
        cell.get().left();
        ruleInfo(cell, text);
        t.row();
    }
}
