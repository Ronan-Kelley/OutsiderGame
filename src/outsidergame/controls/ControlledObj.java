package outsidergame.controls;

public class ControlledObj {
    private final GamepadControllable m_obj;
    private final int m_desiredControllerPort;

    public ControlledObj(GamepadControllable obj, int desiredControllerPort) {
        this.m_obj = obj;
        this.m_desiredControllerPort = desiredControllerPort;
    }

    public GamepadControllable getObj() {
        return m_obj;
    }

    public int getDesiredControllerPort() {
        return m_desiredControllerPort;
    }
}
