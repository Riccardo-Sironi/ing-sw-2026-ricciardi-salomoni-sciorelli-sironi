package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

/**
 * Tells the clients that someone has chosen their designated totem color.
 */
public class ChooseTotemColorDTO implements SmallModelEditor {
    private final String nickname;
    private final Color color;

    /**
     * @param nickname The player locking in a color.
     * @param color The chosen token color shade.
     */
    public ChooseTotemColorDTO(String nickname, Color color) {
        this.nickname = nickname;
        this.color = color;
    }

    /**
     * @return the nickname of the player.
     */
    public String getNickname() { return nickname; }

    /**
     * @return the chosen color by the player.
     */
    public Color getColor() { return color; }

    /**
     * {@inheritDoc}
     * @param visitor
     */
    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     * @param smallModel the client's small model.
     */
    @Override
    public void edit(SmallModel smallModel) {
        if (smallModel.getPlayer().getNickname().equals(nickname)) {
            smallModel.getPlayer().setColor(color);
        }
        else {
            smallModel.getOpponents().stream()
                    .filter(o -> o.getNickname().equals(nickname))
                    .findFirst()
                    .ifPresent(o -> o.setColor(color));
        }
    }
}
