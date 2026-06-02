package it.polimi.gc06.mesos.dtos;

/**
 * Base visitor allowing UI or logic pieces to hook into specific update events.
 * It's all empty defaults, overriden when needed.
 */
public abstract class DTOvisitor {

    /**
     * Overridable hook for generic DTO patches.
     *
     * @param dto the patch getting visited.
     */
    public void visit(SmallModelEditor dto) {
    }

    public void visit(LeaderboardChangeDTO dto) {
        visit((SmallModelEditor) dto);
    }

    /**
     * Overridable hook for generic DTO patches.
     *
     * @param dto the patch getting visited.
     */
    public void visit(BuildingsRefillDTO dto) {
        visit((SmallModelEditor) dto);
    }

    /**
     * Overridable hook for generic DTO patches.
     *
     * @param dto the patch getting visited.
     */
    public void visit(LobbyInitializedDTO dto) {
        visit((SmallModelEditor) dto);
    }

    /**
     * Overridable hook for generic DTO patches.
     *
     * @param dto the patch getting visited.
     */
    public void visit(PickBottomRowDTO dto) {
        visit((SmallModelEditor) dto);
    }

    /**
     * Overridable hook for generic DTO patches.
     *
     * @param dto the patch getting visited.
     */
    public void visit(PickBottomBuildingsDTO dto) {
        visit((SmallModelEditor) dto);
    }

    /**
     * Overridable hook for generic DTO patches.
     *
     * @param dto the patch getting visited.
     */
    public void visit(PickTopRowDTO dto) {
        visit((SmallModelEditor) dto);
    }

    /**
     * Overridable hook for generic DTO patches.
     *
     * @param dto the patch getting visited.
     */
    public void visit(PickTopBuildingsDTO dto) {
        visit((SmallModelEditor) dto);
    }

    /**
     * Overridable hook for generic DTO patches.
     *
     * @param dto the patch getting visited.
     */
    public void visit(PlayerResourcesChangeDTO dto) {
        visit((SmallModelEditor) dto);
    }

    /**
     * Overridable hook for generic DTO patches.
     *
     * @param dto the patch getting visited.
     */
    public void visit(PlayerStateChangeDTO dto) {
        visit((SmallModelEditor) dto);
    }

    /**
     * Overridable hook for generic DTO patches.
     *
     * @param dto the patch getting visited.
     */
    public void visit(TopRowRefillDTO dto) {
        visit((SmallModelEditor) dto);
    }

    /**
     * Overridable hook for generic DTO patches.
     *
     * @param dto the patch getting visited.
     */
    public void visit(TotemOfferMoveDTO dto) {
        visit((SmallModelEditor) dto);
    }

    /**
     * Overridable hook for generic DTO patches.
     *
     * @param dto the patch getting visited.
     */
    public void visit(PhaseChangeDTO dto) {
        visit((SmallModelEditor) dto);
    }

    /**
     * Overridable hook for generic DTO patches.
     *
     * @param dto the patch getting visited.
     */
    public void visit(EraChangeDTO dto) {
        visit((SmallModelEditor) dto);
    }

    /**
     * Overridable hook for generic DTO patches.
     *
     * @param dto the patch getting visited.
     */
    public void visit(RoundChangeDTO dto) {
        visit((SmallModelEditor) dto);
    }

    /**
     * Overridable hook for generic DTO patches.
     *
     * @param dto the patch getting visited.
     */
    public void visit(TotemTurnMoveDTO dto) {
        visit((SmallModelEditor) dto);
    }

    /**
     * Overridable hook for generic DTO patches.
     *
     * @param dto the patch getting visited.
     */
    public void visit(EventResolvedDTO dto) {
        visit((SmallModelEditor) dto);
    }

    /**
     * Overridable hook for generic DTO patches.
     *
     * @param dto the patch getting visited.
     */
    public void visit(ChooseTotemColorDTO dto) {
        visit((SmallModelEditor) dto);
    }

    /**
     * Overridable hook for generic DTO patches.
     *
     * @param dto the patch getting visited.
     */
    public void visit(MesosStartedDTO dto) {
        visit((SmallModelEditor) dto);
    }

    /**
     * Overridable hook for generic DTO patches.
     *
     * @param dto the patch getting visited.
     */
    public void visit(PlayerRecapDTO dto) {
        visit((SmallModelEditor) dto);
    }


    /**
     * Overridable hook for generic DTO patches.
     *
     * @param dto the patch getting visited.
     */
    public void visit(EndGameDTO dto) {
        visit((SmallModelEditor) dto);
    }
}
