package outsidergame.controls;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

public class ControlsManager {
    /**
     * stick angles:
     * straight up is 1
     * straight down is negative 1
     * full left is -1
     * full right is 1
     */
    private ArrayList<ControlledObj> controlledObjs = new ArrayList<ControlledObj>();
    private static ControlsManager instance = new ControlsManager();
    private ControllerManager controllers;
    Timer timer;

    public ControlsManager() {
        controllers = new ControllerManager();
        controllers.initSDLGamepad();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){

            @Override
            public void run() {
                updateRegisteredClasses();
            }

        }, 0, 40);
    }

    public static ControlsManager getInstance() {
        if (instance == null) {
            instance = new ControlsManager();
        }

        return instance;
    }

    public boolean isPluggedIn(int controller) {
        return controllers.getState(controller).isConnected;
    }

    public ControllerState getControllerState(int controller) {
        return controllers.getState(controller);
    }

    public void registerControllableClass(GamepadControllable obj, int desiredController) {
        if (!obj.equals(null)) {
            controlledObjs.add(new ControlledObj(obj, desiredController));
        }
    }

    private void updateRegisteredClasses() {
        ControlledObj[] controlledObjsCopy = new ControlledObj[controlledObjs.size()];
        controlledObjsCopy = controlledObjs.toArray(controlledObjsCopy);

        for (ControlledObj curObj : controlledObjsCopy) {
            if (curObj != null) {
                curObj.getObj().getGamepadState(getControllerState(curObj.getDesiredControllerPort()));
            }
        }
    }
}
