package advdiff;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.content.UnitTypes;
import mindustry.gen.Unit;

public class UnitTypesBoss extends UnitTypes {

    //@Override
    public void applyOutlineColor(Unit unit) {
        if (unit.isBoss()) {
            Draw.mixcol(unit.team.color, Mathf.absin(7.0F, 1.0F));
        }
    }
}
