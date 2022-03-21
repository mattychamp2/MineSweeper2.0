package model;

public class Tile extends AbstractTile {

    private boolean opened;
    private boolean flagged;
    private boolean mined;
    private int explosiveCount;

    public Tile(boolean explosive){
        opened = false;
        flagged = false;
        mined = explosive;
        explosiveCount = 0;
    }

    @Override
    public boolean open() {
        if (isOpened()) {
            return false;
        }
        else{
            opened=true;
            return true;
        }
    }

    @Override
    public void flag() {
        flagged = true;
    }

    @Override
    public void unflag() {
        flagged = false;
    }

    @Override
    public void toggledFlag(){
        flagged= !flagged;
    }

    public void setSafe(){
        mined = false;
    }

    @Override
    public boolean isFlagged() {
        return flagged;
    }

    @Override
    public boolean isExplosive() {
        return mined;
    }

    @Override
    public boolean isOpened() {
        return opened;
    }

    public void setExplosiveCount(int explosiveCount){
        this.explosiveCount=explosiveCount;
    }

    public int getExplosiveCount(){
        return explosiveCount;
    };
}
