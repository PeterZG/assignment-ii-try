package dungeonmania.entities.logicSwitches.LogicalEntities;

import java.util.List;

import dungeonmania.entities.logicSwitches.Conductor;
import dungeonmania.entities.logicSwitches.ConnectAble;
import dungeonmania.entities.logicSwitches.LogicSwitch;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class LogicalEntity extends LogicSwitch {
    private ConnectAble logicRule;

    public LogicalEntity(Position position, ConnectAble logicRule) {
        super(position);
        this.logicRule = logicRule;
    }

    public ConnectAble getLogicRule() {
        return logicRule;
    }


    public void activate(GameMap map) {
        List<Conductor> adjConductors = getAdjacentConductors(map);

        setActive(getLogicRule().checkCanActivate(adjConductors));
    }
}
