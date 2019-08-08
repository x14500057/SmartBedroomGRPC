/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SmartWardrobe;

/**
 *
 * @author Paul
 */
public class Hanger {
   
            public int xCoor; 
            public int yCoor; 
            public int zCoor;
            public CItem cItem;
            
            public Hanger (int xCoor, int yCoor, int zCoor, CItem cItem) 
            { 
                this.xCoor = xCoor;
                this.yCoor = yCoor;
                this.zCoor = zCoor;
                this.cItem = cItem;
            } 

    public int getxCoor() {
        return xCoor;
    }

    public void setxCoor(int xCoor) {
        this.xCoor = xCoor;
    }

    public int getyCoor() {
        return yCoor;
    }

    public void setyCoor(int yCoor) {
        this.yCoor = yCoor;
    }

    public int getzCoor() {
        return zCoor;
    }

    public void setzCoor(int zCoor) {
        this.zCoor = zCoor;
    }

    public CItem getcItem() {
        return cItem;
    }

    public void setcItem(CItem cItem) {
        this.cItem = cItem;
    }
}
