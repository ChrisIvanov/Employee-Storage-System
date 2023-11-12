package model;

import model.contracts.fireable;

public class AbstractEmployee implements fireable {
    @Override
    public void setIsFired(boolean isFired) {
    }

    @Override
    public boolean getIsFired() {
        return false;
    }
}
