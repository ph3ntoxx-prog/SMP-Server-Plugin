package de.simon.legacyCraft.nether;

public class NetherManager {

    private boolean netherOpen = true; // Startzustand offen

    public boolean isNetherOpen() {
        return netherOpen;
    }

    public void setNetherOpen(boolean netherOpen) {
        this.netherOpen = netherOpen;
    }
}
