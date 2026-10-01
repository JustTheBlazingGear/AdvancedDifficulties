package advdiff.visual;

import arc.Events;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.game.EventType;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;

public class BossOutlineRenderer {

    public static void init() {
        Events.run(EventType.Trigger.draw, () -> {
            Groups.unit.each(unit -> {
                if(unit.isBoss()){
                    drawOutline(unit);
                }
            });
        });
    }

    private static void drawOutline(Unit unit) {
        Draw.mixcol(unit.team.color, unit.team.color, Mathf.absin(7f, 1f));
        float offset = (unit.hitSize / 30 + 0.35f) * Mathf.absin(7f, 1f);
        TextureRegion tex = new TextureRegion(unit.type.region);

        Draw.z(unit.type.groundLayer);
        if (unit.type.flying) Draw.z(Layer.flyingUnitLow);

        Draw.rect(tex, unit.x - offset, unit.y - offset, unit.rotation - 90);
        Draw.rect(tex, unit.x + offset, unit.y + offset, unit.rotation - 90);
        Draw.rect(tex, unit.x + offset, unit.y - offset, unit.rotation - 90);
        Draw.rect(tex, unit.x - offset, unit.y + offset, unit.rotation - 90);

        Draw.reset();
    }
}
