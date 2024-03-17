public class Animation extends Action{
    private int repeatCount;

    public Animation(Actions entity, int repeatCount) {
        super(entity);
        this.repeatCount = repeatCount;
    }

    @Override
    public void execute(EventScheduler scheduler) {
        getEntity().updateImage();

        if (repeatCount != 1) {
            scheduler.scheduleEvent(getEntity(), new Animation(getEntity(), Math.max(this.repeatCount - 1, 0)), getEntity().getAnimationPeriod());
        }
    }
}
