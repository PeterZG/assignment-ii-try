package dungeonmania.entities.logicSwitches.LogicalEntities;

import dungeonmania.entities.Entity;
import dungeonmania.entities.logicSwitches.ConnectAble;
import dungeonmania.util.Position;

public class LightBulb extends LogicalEntity {
    public LightBulb(Position position, ConnectAble logicRule) {
        super(position.asLayer(Entity.ITEM_LAYER), logicRule);
    }
}
