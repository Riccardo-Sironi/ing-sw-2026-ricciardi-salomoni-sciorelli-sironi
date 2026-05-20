package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

public class ChooseTotemColorDTO implements SmallModelEditor {
    private final String nickname;
    private final Color color;

    public ChooseTotemColorDTO(String nickname, Color color) {
        this.nickname = nickname;
        this.color = color;
    }

    public String getNickname() { return nickname; }
    public Color getColor() { return color; }

    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }

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
