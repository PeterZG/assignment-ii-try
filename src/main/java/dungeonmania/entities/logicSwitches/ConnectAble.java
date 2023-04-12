package dungeonmania.entities.logicSwitches;

import java.util.List;

public interface ConnectAble {
    public boolean checkCanActivate(List<Conductor> conductors);
}
