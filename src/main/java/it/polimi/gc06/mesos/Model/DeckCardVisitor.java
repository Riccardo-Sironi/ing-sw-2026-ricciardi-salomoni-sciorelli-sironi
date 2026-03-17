package it.polimi.gc06.mesos.Model;

public class DeckCardVisitor implements TribeCardVisitor{

    private final Player player;

    DeckCardVisitor(Player player){
        this.player = player;
    }

    @Override
    public void visit(RitualEvent ritual) {

    }

    @Override
    public void visit(SustenanceEvent sustenance) {

    }

    @Override
    public void visit(HuntEvent hunt) {

    }

    @Override
    public void visit(PaintingsEvent paintings) {

    }

    @Override
    public void visit(HunterCard card) {
        player.getCharacterDeck().get(CharacterType.HUNTER).add(card);
    }

    @Override
    public void visit(ShamanCard card) {
        player.getCharacterDeck().get(CharacterType.SHAMAN).add(card);
    }

    @Override
    public void visit(ArtistCard card) {
        player.getCharacterDeck().get(CharacterType.ARTIST).add(card);
    }

    @Override
    public void visit(BuilderCard card) {
        player.getCharacterDeck().get(CharacterType.BUILDER).add(card);
    }

    @Override
    public void visit(InventorCard card) {
        player.getCharacterDeck().get(CharacterType.INVENTOR).add(card);
    }

    @Override
    public void visit(GathererCard card) {
        player.getCharacterDeck().get(CharacterType.GATHERER).add(card);
    }

    //default case
    @Override
    public void visit(TribeCard card) {

    }
}
