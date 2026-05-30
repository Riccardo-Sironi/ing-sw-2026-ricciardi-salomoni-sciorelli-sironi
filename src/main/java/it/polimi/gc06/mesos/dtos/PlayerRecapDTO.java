package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.cards.characters.InventionIcon;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

import java.util.Set;

public class PlayerRecapDTO implements SmallModelEditor{

    //everything can be null
    private final String nickname;
    private final Integer buildersDiscount;
    private final Integer shamanStar;
    private final Integer artistNumber;
    private final Integer builderNumber;
    private final Integer gathererNumber;
    private final Integer hunterNumber;
    private final Integer inventorNumber;
    private final Integer shamanNumber;
    private final Set<InventionIcon> collectedIcons;
    private Integer sequenceNumber;

    public PlayerRecapDTO(String nickname, Integer buildersDiscount, Integer shamanStar, Integer artistNumber,
                          Integer builderNumber, Integer gathererNumber, Integer hunterNumber, Integer inventorNumber,
                          Integer shamanNumber, Set<InventionIcon> collectedIcons) {
        this.nickname = nickname;
        this.buildersDiscount = buildersDiscount;
        this.shamanStar = shamanStar;
        this.artistNumber = artistNumber;
        this.builderNumber = builderNumber;
        this.gathererNumber = gathererNumber;
        this.hunterNumber = hunterNumber;
        this.inventorNumber = inventorNumber;
        this.shamanNumber = shamanNumber;
        this.collectedIcons = collectedIcons;
        this.sequenceNumber = null;
    }

    public PlayerRecapDTO(String nickname, Integer shamanNumber, Integer inventorNumber, Integer hunterNumber,
                          Integer gathererNumber, Integer builderNumber, Integer artistNumber) {
        this.nickname = nickname;
        this.shamanNumber = shamanNumber;
        this.inventorNumber = inventorNumber;
        this.hunterNumber = hunterNumber;
        this.gathererNumber = gathererNumber;
        this.builderNumber = builderNumber;
        this.artistNumber = artistNumber;
        this.collectedIcons = null;
        this.buildersDiscount = null;
        this.shamanStar = null;
        this.sequenceNumber = null;
    }


    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException, Error {
        //finds the player by nickname
        PlayerView player = smallModel.getPlayer().getNickname().equals(nickname) ? smallModel.getPlayer() : null;
        if(player == null) player = smallModel.getOpponents().stream().filter(pv -> pv.getNickname().equals(nickname))
                .findFirst().orElse(null);
        if(player == null) throw new IllegalStateException("Nickname given not present in smallModel");
        //sets all not-null parameters
        if(shamanNumber != null) player.setShamanNumber(shamanNumber);
        if(inventorNumber != null) player.setInventorNumber(inventorNumber);
        if(hunterNumber != null) player.setHunterNumber(hunterNumber);
        if(gathererNumber != null) player.setGathererNumber(gathererNumber);
        if(builderNumber != null) player.setBuilderNumber(builderNumber);
        if(artistNumber != null) player.setArtistNumber(artistNumber);
        if(collectedIcons != null) player.setCollectedIcons(collectedIcons);
        if(buildersDiscount != null) player.setBuildersDiscount(buildersDiscount);
        if(shamanStar != null) player.setShamanStar(shamanStar);
    }

    /**
     * {@inheritDoc}
     * @param visitor
     */
    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Get the player's nickname.
     *
     * @return the player's nickname
     */
    public String getPlayer(){
        return nickname;
    }

    @Override
    public void setSequenceNumber(int sNum) {
        sequenceNumber = sNum;
    }

    @Override
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

}
