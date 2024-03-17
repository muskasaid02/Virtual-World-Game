public class Behavior extends Action {
    private World world;
    private ImageLibrary imageLibrary;

    /**
     * Constructs an Action object with specified characteristics.
     * In the base program, this is not called directly.
     * Instead, the encapsulated 'create' method are used to create specific kinds.
     *
     * @param entity The entity enacting the action.
     */
    public Behavior(Actions entity, World world, ImageLibrary imageLibrary) {
        super(entity);
        this.imageLibrary = imageLibrary;
        this.world = world;
    }

    public void execute(EventScheduler scheduler) {
        getEntity().executeBehavior(world, imageLibrary, scheduler);

    }
}
