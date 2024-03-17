/** A scheduled action to be carried out by a specific entity. */
public abstract class Action {
    /**
     * Enumerated type defining different kinds of actions that entities take in the world.
     * Specific values are assigned to the action's 'kind' instance variable at initialization.
     * There are two types of actions: animations (image updates) and behaviors (logic updates).
     */

    private final Actions entity;
    /**
     * Constructs an Action object with specified characteristics.
     * In the base program, this is not called directly.
     * Instead, the encapsulated 'create' method are used to create specific kinds.
     *
     * @param entity The entity enacting the action.
     */
    public Action(Actions entity) {
        this.entity = entity;
    }

    /** Called when the action's scheduled time occurs. */
    public abstract void execute(EventScheduler scheduler);

    public Actions getEntity() {return entity; }
}
