package fuzs.puzzlesapi.api.client.iteminteractions.v1.tooltip;

import fuzs.puzzlesapi.api.iteminteractions.v1.tooltip.ContainerItemTooltip;

public class ClientContainerItemTooltip extends AbstractClientContainerItemTooltip {
    private final int gridSizeX;
    private final int gridSizeY;

    public ClientContainerItemTooltip(ContainerItemTooltip tooltip) {
        super(tooltip.items(), tooltip.backgroundColor());
        this.gridSizeX = tooltip.gridSizeX();
        this.gridSizeY = tooltip.gridSizeY();
    }

    @Override
    protected int getGridSizeX() {
        return this.gridSizeX;
    }

    @Override
    protected int getGridSizeY() {
        return this.gridSizeY;
    }
}
